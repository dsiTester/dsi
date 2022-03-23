if [ $# -ne 2 ]; then
    echo "USAGE: $0 DSI_EXPERIMENTS_DIR PROJECT"
    exit
fi

SCRIPT_DIR=$( cd $( dirname $0 ) && pwd )
REPO_DIR=$( dirname "${SCRIPT_DIR}" )
INSPECTIONS_DIR=${REPO_DIR}/inspections
DSI_EXPERIMENT_DIR=$1
PROJECT=$2
OUT_DIR=${SCRIPT_DIR}/javert-bddminer-data/
mkdir -p ${OUT_DIR}

function main() {
    # for project in $( ls ${INSPECTIONS_DIR} ); do
    local project=${PROJECT}
    echo =============================================================${project}
    bdd_dir=${OUT_DIR}/${project}/bdd
    javert_dir=${OUT_DIR}/${project}/javert
    mkdir -p ${bdd_dir}
    mkdir -p ${javert_dir}
    mkdir -p ${bdd_dir}/ab
    mkdir -p ${bdd_dir}/ab@s
    mkdir -p ${bdd_dir}/abp
    mkdir -p ${bdd_dir}/apb
    mkdir -p ${bdd_dir}/abs
    mkdir -p ${bdd_dir}/asb
    mkdir -p ${bdd_dir}/abq@q/
    mkdir -p ${bdd_dir}/aqb@q/
    mkdir -p ${bdd_dir}/abq@s/
    mkdir -p ${bdd_dir}/aqb@s/
    mkdir -p ${bdd_dir}/apbp@q/
    mkdir -p ${bdd_dir}/apbs@q/
    mkdir -p ${bdd_dir}/asbp@q/
    mkdir -p ${bdd_dir}/apbp@s/
    mkdir -p ${bdd_dir}/apbs@s/
    mkdir -p ${bdd_dir}/asbp@s/
    tmpdir=${SCRIPT_DIR}/other-projects-tmp-${project}
    if [ ! -d ${tmpdir} ]; then
	# rm -rf ${tmpdir}
	echo "copying traces..."
        cp -r ${DSI_EXPERIMENT_DIR}/dsiPlus-dsiAllGranularities-${project}/${project}/dsiPlus-dsiAllGranularities/ws/traces/ ${tmpdir}
    fi
    echo "done copying traces..."
    cd ${tmpdir}
    ls | grep -v "method-names.txt.gz" | grep -v "processed.txt" | rev | cut -d. -f3- | rev | sed "s/$/@${project}/g" |  parallel -j 96 bash ${SCRIPT_DIR}/other-miners-helper.sh
}

main
