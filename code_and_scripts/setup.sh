SCRIPT_DIR=$( cd $( dirname $0 ) && pwd )

mv ~/.m2 ~/.m2-backup
cp -r ${SCRIPT_DIR}/dot_m2 ~/.m2
