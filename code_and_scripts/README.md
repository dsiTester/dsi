# Artifact and Reproduction

We provide a Dockerfile to set up the environment for running the DSI+ tool implementation.

## Running DSI+ and reproducing results
1. Build a docker container using `dockerFile` and run.
   ```
   docker build -t dsi:test .
   docker run -it dsi:test
   ```
2. Run `bash setup.sh` to set up the environment for DSI+ tool implementation to run. This script will move the local `~/.m2` file to `~/.m2-backup`, so this should be done in a docker container to be safe. The first time this script is invoked in the Docker, `mv` will show an error because there is no current `~/.m2` directory.
3. The script `run-small-project.sh` will run DSI+ and DSI++ on the shortest running project (kamranzafar.jtar). Running this script takes an estimated 5 minutes to run on a server with 32 cores.
4. The command `bash run-inspected-projects.sh ${MINING_OP}` will run DSI+ and DSI++ on the set of the 6 inspected projects. Here, `${MINING_OP}` should be set to `-noMining` to avoid mining.
5. The script `bash run-all-projects.sh ${MINING_OP}` will run DSI+ and DSI++ on all 36 subject projects. Here, `${MINING_OP}` should be set to `-noMining` to avoid mining. Know that some projects may take very long and may need to be manually stopped.

The set of true specifications from DSI/DSI+/DSI+ optimizations can be found in `data/generated-data/<PROJECT_NAME>/<DSI+CONFIGURATION>/results/total-true-specs.txt`. `<DSI+CONFIGURATION>` is one of the following:

- `dsiPlus-dsiAllGranularities`: Run DSI+ on all test granularities (ALL TESTS, TEST CLASSES, TEST METHODS)
- `dsiPlus-allGranularities`: Run DSI++ on all test granularities

The files for spurious and unknown specs are in the similar paths but with (hopefully) intuitive names.

## Running Javert and BDDMiner

1. Run `bash run-dsi-plus-experiments.sh` _without_ the `-noMining` option to collect traces from the specific project.
2. Run `bash retrieve-specs-from-other-miners.sh data/generated-data/ ${project}` to mine Javert and BDDMiner specifications from the same project.
3. Run `python3 analyze-specs-from-other-miners.py javert-bddminer-data` to process the output. (Note that `inspections/scripts/create_inspections_with_tag_field.py` need to be run first).
