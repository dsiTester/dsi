import collections
import copy
import json
import os
import sys
import csv
from create_inspections_with_tag_field import get_tags
from pathlib import Path
from shutil import rmtree
import subprocess
import matplotlib.pyplot as plt
from matplotlib_venn import venn3
from matplotlib_venn import venn3_unweighted

scripts_dir=os.path.dirname(os.path.abspath(__file__))
base_dir=os.path.join(os.path.dirname(scripts_dir), "inspections")
tagged_inspections_dir=os.path.join(base_dir, "tagged-inspections")
intermediate_data_dir = os.path.join(base_dir, "data")
output_dir=os.path.join(intermediate_data_dir, "analysis")

"""
Utility function to write output to csv.
"""
def output_to_csv(keys, list_to_output, dirname, filename):
    if not os.path.exists(dirname):
        os.mkdir(dirname)
    with open(os.path.join(dirname, filename), "w") as out:
        writer = csv.DictWriter(out, fieldnames = keys)
        writer.writerows(list_to_output)

def get_spurious_specs(project_name):
    dsi_tn_specs=[]
    manual_spurious_specs = []
    nbp_specs = []
    project_dir=os.path.join(tagged_inspections_dir, project_name)
    for filename in os.listdir(project_dir):
        if not filename.endswith(".json"):
            continue
        f = open(os.path.join(project_dir, filename))
        data = json.load(f)
        for spec_data in data:
            spec = spec_data["method-a"] + " " + spec_data["method-b"]
            if "no-break-pass" in spec_data["verdict"] or "SPECIAL_NBP" in spec_data["tags"]:
                nbp_specs.append(spec)
            elif spec_data["verdict"] == "spurious-spec":
                manual_spurious_specs.append(spec)
                if filename == "ls.json":
                    dsi_tn_specs.append(spec)
    # print(len(nbp_specs))
    return manual_spurious_specs, dsi_tn_specs, nbp_specs

def get_bdd_specs_from_pattern(bd, cat, catdirname):
    cat_dir = os.path.join(bd, catdirname)
    specs = set()
    if not os.path.exists(cat_dir):
        return specs
    for bf in os.listdir(cat_dir):
        if cat == "ab":
            test_name="".join(bf.split("-")[2:])
        else:
            test_name="".join(bf.split("-")[3:])
        if test_name == "alltests":
            test_name = "all-tests"
        with open(os.path.join(cat_dir, bf)) as f:
            in_spec = False
            for line in f:
                if line.startswith("Pattern: "):
                    in_spec = True
                elif in_spec and "a = " in line:
                    a = line.split()[2]
                elif in_spec and "b = " in line:
                    b = line.split()[2]
                    specs.add(a + " " + b)
                    in_spec = False
    return specs

def get_specs_from_bdd(miners_out_dir):
    cat_to_specs = {}
    bd = os.path.join(miners_out_dir, "bdd")
    # cats2dirname = { "(ab)*" : "ab@s" , "ab*" : "abs" , "a*b" : "asb", "a+b" : "apb", "ab+" : "abp", "ab" : "ab"}
    # for cat in cats2dirname:
    cats2dirname = {}
    for cat in os.listdir(bd):
        cats2dirname[cat] = cat
        cat_to_specs[cat] = get_bdd_specs_from_pattern(bd, cat, cats2dirname[cat])
    return cat_to_specs

def get_specs_from_javert(miners_out_dir):
    jd = os.path.join(miners_out_dir, "javert")
    if not os.path.exists(jd):
        return []
    specs = {}
    for jf in os.listdir(jd):
        test_name="".join(jf.split("-")[2:])
        if test_name == "alltests":
            test_name = "all-tests"
        with open(os.path.join(jd, jf)) as f:
            in_spec=False
            spec_str_acc=""
            spec_lst_acc=[]
            contains_test_class = False
            for line in f:
                if not line.strip():
                    continue
                if line.startswith("Specification "): # found a spec
                    spec_num = line.split()[1]
                    in_spec = True
                elif in_spec and line.startswith("----"): # done reading current spec
                    if spec_str_acc in specs:
                        specs[spec_str_acc]["tests"].append(test_name)
                    else:
                        specs[spec_str_acc] = {"spec-lst" : spec_lst_acc, "tests": [test_name], "contains-test-class" : contains_test_class}
                    spec_str_acc=""
                    spec_lst_acc=[]
                    in_spec = False
                    contains_test_class = False
                elif in_spec:
                    if "Test" in line or "test" in line:
                        contains_test_class = True
                    spec_str_acc += line
                    spec_lst_acc.append(line.strip())
    return specs

"""

"""
def compare_notes_bdd(name, cat2specs, man, dsi, nbp, simple_mined):
    mined = 0
    test_class = 0
    spurious_by_dsi = 0
    spurious_by_manual = 0
    num_nbp = 0
    mined_by_simple = 0
    all_specs = set()
    for pattern in cat2specs:
        all_specs = all_specs.union(cat2specs[pattern])
    # for spec in cat2specs[pattern]:
    for spec in all_specs:
        mined += 1
        if "Test" in spec or "test" in spec:
            test_class += 1
            continue
        if spec in dsi:
            spurious_by_dsi += 1
        if spec in man:
            spurious_by_manual += 1
        if spec in nbp:
            num_nbp += 1
        if spec in simple_mined:
            mined_by_simple += 1
    return {"project": name, "mined" : mined, "spurious-by-dsi" : spurious_by_dsi, "spurious-by-manual": spurious_by_manual, "nbp" : num_nbp, "simple-miner-also-mined" : mined_by_simple, "test-class" : test_class}

"""
javert_specs: str_identifier --> {"spec-lst" : list form of the spec, "tests": tests that mined it}
man: manual spurious specs
dsi: dsi spurious specs
"""
def compare_notes_javert(name, javert_specs, man, dsi, nbp, simple_mined):
    man_matched = set()
    dsi_matched = set()
    man_sp_match = 0
    man_sp_nonmatch = 0
    dsi_tn_match = 0
    dsi_tn_nonmatch = 0
    evaluated_specs = set()
    bigger_specs_with_bug = set()
    bigger_specs_with_bug_caught_by_dsi = set()
    not_in_nbp = 0
    bigger_specs_with_no_nbp = set()
    num_simple_not_mined = 0
    num_javert_more_than_two_letters = 0
    num_javert_2_also_simple= 0 # number of 2 letter specs mined by javert 
    num_javert_2_also_simple_man_spurious = 0 # also spurious
    javert_more_than_2_spurious = set()       # number of >2 size spec that has a spurious edge
    num_contains_test_class = 0
    for javert_spec_str in javert_specs:
        javert_spec_lst = javert_specs[javert_spec_str]["spec-lst"]
        if javert_specs[javert_spec_str]["contains-test-class"]:
            # print(javert_spec_str)
            num_contains_test_class += 1
        if len(javert_spec_lst) <= 4:
            a = ""
            b = ""
            for item in javert_spec_lst:
                if not (item.startswith("(") or item.startswith(")") ):
                    if a == "":
                        a = item
                    elif b == "":
                        b = item
                    else:
                        print("what")
            s = a + " " + b
            if s in simple_mined:
                num_javert_2_also_simple += 1
                if s in man:
                    num_javert_2_also_simple_man_spurious += 1
        else:
            num_javert_more_than_two_letters += 1
            # this is so growss
            for i in range(len(javert_spec_lst)-1):
                i_mtd = javert_spec_lst[i]
                j_mtd = javert_spec_lst[i+1]
                if (not (i_mtd.startswith("(") or i_mtd.startswith(")") or "|" in i_mtd)) and (not (j_mtd.startswith("(") or j_mtd.startswith(")") or "|" in j_mtd)):
                    spec = i_mtd + " " + j_mtd
                    if spec in man:
                        javert_more_than_2_spurious.add(javert_spec_str)
        for i in range(len(javert_spec_lst)-1):
            i_mtd = javert_spec_lst[i]
            j_mtd = javert_spec_lst[i+1]
            if (not (i_mtd.startswith("(") or i_mtd.startswith(")") or "|" in i_mtd)) and (not (j_mtd.startswith("(") or j_mtd.startswith(")") or "|" in j_mtd)):
                spec = i_mtd + " " + j_mtd
                if spec in evaluated_specs:
                    if (spec in man_matched):
                        bigger_specs_with_bug.add(javert_spec_str)
                    if (spec in dsi_matched):
                        bigger_specs_with_bug_caught_by_dsi.add(javert_spec_str)
                    continue
                if not spec in simple_mined:
                    num_simple_not_mined += 1
                # print(spec)
                if not spec in nbp:
                    not_in_nbp += 1
                    bigger_specs_with_no_nbp.add(javert_spec_str)
                if spec in man:
                    man_sp_match += 1
                    man_matched.add(spec)
                    bigger_specs_with_bug.add(javert_spec_str)
                elif not spec in nbp:
                    man_sp_nonmatch += 1
                if spec in dsi:
                    dsi_tn_match += 1
                    dsi_matched.add(spec)
                    bigger_specs_with_bug_caught_by_dsi.add(javert_spec_str)
                elif not spec in nbp:
                    dsi_tn_nonmatch += 1
                evaluated_specs.add(spec)

    # num_javert_2_also_simple= 0 # number of 2 letter specs mined by javert 
    # num_javert_2_also_simple_man_spurious = 0 # also spurious
    # javert_more_than_2_spurious = set()       # number of >2 size spec that has a spurious edge

    return {"project" : name, "man_sp_match" : man_sp_match, "man_sp_nonmatch" : man_sp_nonmatch, "dsi_tn_match" : dsi_tn_match, "dsi_tn_nonmatch" : dsi_tn_nonmatch, "specs-with-bugs" : len(bigger_specs_with_bug), "specs-with-bugs-caught-by-dsi" : len(bigger_specs_with_bug_caught_by_dsi), "total-smaller-specs" : len(evaluated_specs), "total-bigger-specs" : len(javert_specs), "smaller-specs-not-nbp" : not_in_nbp, "bigger-specs-not-nbp" : len(bigger_specs_with_no_nbp), "num-2L-not-simple-mined" : num_simple_not_mined, "num_javert_more_than_two_letters": num_javert_more_than_two_letters, "num_javert_2_also_simple" : num_javert_2_also_simple, "num_javert_2_also_simple_man_spurious" : num_javert_2_also_simple_man_spurious, "num_javert_more_than_2_spurious" : len(javert_more_than_2_spurious), "num-javert-contains-test-class" : num_contains_test_class }

def read_simple_miner_mined_specs(project_name):
    smaps = os.path.join(intermediate_data_dir, "spec-to-test-maps")
    mined_specs=[]
    with open(os.path.join(smaps, project_name + "-master-spec-file.txt")) as f:
        for line in f:
            a=line.split()[1].split("=")[1]
            b=line.split()[2].split("=")[1]
            mined_specs.append(a + " " + b)

    return mined_specs

def javert_wrapper(other_miners_dir):
    keys = ["project", "man_sp_match", "man_sp_nonmatch", "dsi_tn_match", "dsi_tn_nonmatch", "smaller-specs-not-nbp", "num-2L-not-simple-mined", "total-smaller-specs", "specs-with-bugs", "specs-with-bugs-caught-by-dsi", "bigger-specs-not-nbp", "total-bigger-specs", "num_javert_more_than_two_letters", "num_javert_more_than_2_spurious", "num_javert_2_also_simple",  "num_javert_2_also_simple_man_spurious", "num-javert-contains-test-class"]
    header = {"project" : "project", "man_sp_match" : "spurious-by-manual", "man_sp_nonmatch" : "not-in-manual-spurious", "dsi_tn_match" : "spurious-by-dsi", "dsi_tn_nonmatch" : "not-in-dsi-ts", "specs-with-bugs" : "num-javert-w-bugs", "specs-with-bugs-caught-by-dsi" : "num-javert-dsi-caught", "total-smaller-specs" : "total-2L", "total-bigger-specs" : "total-javert", "smaller-specs-not-nbp" : "total-2L-not-nbp", "bigger-specs-not-nbp": "total-javert-not-nbp", "num-2L-not-simple-mined": "num-2L-not-simple-mined", "num_javert_more_than_two_letters": "num-javert->2-letters", "num_javert_2_also_simple" : "num-javert-2-also-simple", "num_javert_2_also_simple_man_spurious" : "num_javert_2_also_simple_man_spurious", "num_javert_more_than_2_spurious" : "num_javert_more_than_2_spurious", "num-javert-contains-test-class" : "num-javert-contains-test-class"}
    javert_out_list = []
    for project_name in os.listdir(other_miners_dir):
        simple_mined_specs = read_simple_miner_mined_specs(project_name)
        man, dsi_tn, nbp = get_spurious_specs(project_name)
        other_miners_project_dir = os.path.join(other_miners_dir, project_name)
        javert_specs = get_specs_from_javert(other_miners_project_dir)
        # for spec in javert_specs:
        #     print(javert_specs[spec]["tests"])
        # print("=======================================================man")
        # for spec in man:
        #     print(spec)
        # print("=======================================================dsi")
        # for spec in dsi_tn:
        #     print(spec)
        # print("========================================================javert")
        javert_out_list.append(compare_notes_javert(project_name, javert_specs, man, dsi_tn, nbp, simple_mined_specs))

    total = { key : 0 for key in keys }
    total["project"] = "total"
    for jmap in javert_out_list:
        for key in total:
            if key == "project":
                continue
            total[key] += jmap[key]

    javert_out_list.insert(0, header)
    javert_out_list.append(total)
    output_to_csv(keys, javert_out_list, output_dir, "javert.csv")

def bdd_wrapper(other_miners_dir):
    keys = ["project", "mined", "test-class", "spurious-by-dsi", "spurious-by-manual", "nbp", "simple-miner-also-mined"]
    header = {key : key for key in keys}
    bdd_out_list = []
    for project_name in os.listdir(other_miners_dir):
        other_miners_project_dir = os.path.join(other_miners_dir, project_name)
        bdd_spec_map = get_specs_from_bdd(other_miners_project_dir)
        # print("=====================" + project_name)
        # for pattern in bdd_spec_map:
        simple_mined_specs = read_simple_miner_mined_specs(project_name)
        man, dsi_tn, nbp = get_spurious_specs(project_name)
        bdd_out_list.append(compare_notes_bdd(project_name, bdd_spec_map, man, dsi_tn, nbp, simple_mined_specs))
        # print("pattern: " + pattern + " # specs: " + str(len(bdd_spec_map[pattern])))
    total = { key : 0 for key in keys }
    total["project"] = "total"
    for bmap in bdd_out_list:
        for key in total:
            if key == "project":
                continue
            total[key] += bmap[key]
    bdd_out_list.insert(0, header)
    bdd_out_list.append(total)
    output_to_csv(keys, bdd_out_list, output_dir, "bdd.csv")

def wrapper(other_miners_dir):
    if not os.path.exists(other_miners_dir):
        print("Other miners dir not found!")
        sys.exit(1)
    javert_wrapper(other_miners_dir)
    bdd_wrapper(other_miners_dir)

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: " + sys.argv[0] + " other-miners-out-dir")
        sys.exit(1)
    wrapper(sys.argv[1])
