#!/bin/sh

if [ $# != 1 ]; then
    echo "usage: bash $0 CONFIG"
    exit
fi

CONFIG=$1

id=$( echo "${CONFIG}" | cut -d'@' -f1 )
a=$( echo "${CONFIG}" | cut -d'@' -f2 )
b=$( echo "${CONFIG}" | cut -d'@' -f3 )
tests=$( echo "${CONFIG}" | cut -d'@' -f4 )
optimization_op_num=$( echo "${CONFIG}" | cut -d'@' -f5 )

SCRIPT_DIR=$( cd $( dirname $0 ) && pwd )
project_location=`dirname ${PWD}`
comp_log_file=${project_location}/logs/gol-compile-${id}
log_file=${project_location}/logs/gol-spec-based-${id}
master_spec_file=${project_location}/logs/master-spec-file.txt
total_spec_num=$( cat ${master_spec_file} | wc -l )

if [ ${optimization_op_num} -eq 2 ]; then
    optimization_op="-DfailEarlyOp=fail-on-spurious -DselectionOp=no-selection"
elif [ ${optimization_op_num} -eq 3 ]; then
    optimization_op="-DfailEarlyOp=run-all -DselectionOp=selection"
elif [ ${optimization_op_num} -eq 4 ]; then
    optimization_op="-DfailEarlyOp=fail-on-spurious -DselectionOp=selection"
elif [ ${optimization_op_num} -eq 5 ]; then
    optimization_op="-DfailEarlyOp=fail-on-not-true -DselectionOp=no-selection"
elif [ ${optimization_op_num} -eq 6 ]; then
    optimization_op="-DfailEarlyOp=fail-on-not-true -DselectionOp=selection"
else # if we don't get either 2/3/4/5/6 optimization, we will run the default (no further optimizations besides one pass)
    optimization_op="-DfailEarlyOp=run-all -DselectionOp=no-selection"
fi

echo "RUNNING DSI+ ON SPEC ID ${id} out of total ${total_spec_num} specs with optimization options ${optimization_op}"

# copying over the originally compiled target directory to avoid extra work...
cp -r target ${id}

( time mvn dsi:run-spec-based-dsi ${SKIPS} -DskipTraceCollection=true -Did=${id} -Da=${a} -Db=${b} -DbuildDirectory=${id} -DtempDir=${id} -DtestsToRun=${tests} ${optimization_op} ) >> ${log_file} 2>&1

# delete to save space
rm -rf ${id}
