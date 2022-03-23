input=$1
tst=$( echo "${input}" | cut -d@ -f1 )
project=$( echo "${input}" | cut -d@ -f2 )

SCRIPT_DIR=$( cd $( dirname $0 ) && pwd )
REPO_DIR=$( dirname "${SCRIPT_DIR}" )
OUT_DIR=${SCRIPT_DIR}/javert-bddminer-data/
bdd_dir=${OUT_DIR}/${project}/bdd
javert_dir=${OUT_DIR}/${project}/javert

echo "$tst"

if [ ! -f ${tst}.txt ]; then
    gunzip ${tst}.txt.gz
fi
cut -d' ' -f3 ${tst}.txt > ${tst}-processed.txt
timeout 100m java -jar ${SCRIPT_DIR}/javert.jar -flat ${tst}.txt &> ${javert_dir}/gol-javert-${tst}
timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "ab" ${tst}-processed.txt &> ${bdd_dir}/ab/gol-bdd-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(ab)*" ${tst}-processed.txt &> ${bdd_dir}/ab@s/gol-bdd-ab@s-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "ab+" ${tst}-processed.txt &> ${bdd_dir}/abp/gol-bdd-abp-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "a+b" ${tst}-processed.txt &> ${bdd_dir}/apb/gol-bdd-apb-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "a*b" ${tst}-processed.txt &> ${bdd_dir}/abs/gol-bdd-abs-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "ab*" ${tst}-processed.txt &> ${bdd_dir}/asb/gol-bdd-asb-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(ab?)?" ${tst}-processed.txt &> ${bdd_dir}/abq@q/gol-bdd-abq@q-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(a?b)?" ${tst}-processed.txt &> ${bdd_dir}/aqb@q/gol-bdd-aqb@q-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(ab?)*" ${tst}-processed.txt &> ${bdd_dir}/abq@s/gol-bdd-abq@s-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(a?b)*" ${tst}-processed.txt &> ${bdd_dir}/aqb@s/gol-bdd-aqb@s-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(a+b+)?" ${tst}-processed.txt &> ${bdd_dir}/apbp@q/gol-bdd-apbp@q-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(a+b*)?" ${tst}-processed.txt &> ${bdd_dir}/apbs@q/gol-bdd-apbs@q-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(a*b+)?" ${tst}-processed.txt &> ${bdd_dir}/asbp@q/gol-bdd-asbp@q-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(a+b+)*" ${tst}-processed.txt &> ${bdd_dir}/apbp@s/gol-bdd-apbp@s-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(a+b*)*" ${tst}-processed.txt &> ${bdd_dir}/apbs@s/gol-bdd-apbs@s-${tst}
# timeout 100m java -jar ${SCRIPT_DIR}/bddminer.jar -mine "(a*b+)*" ${tst}-processed.txt &> ${bdd_dir}/asbp@s/gol-bdd-asbp@s-${tst}
