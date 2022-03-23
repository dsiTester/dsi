# RQ4: Specification and Test Interaction

The partitioning based on return values within this directory are between void and non-void. Files in this directory:

- `return-values-all-specs.csv` contains the categorization of all inspected specifications based on the return values of `a()` and `b()`.
- `return-values-false-negative-specs.csv` contains the categorization of all FN specifications (DSI+ classified as likely spurious, manual inspection classified as true specification) based on the return values of `a()` and `b()`.
- `return-values-false-positive-specs.csv` contains the categorization of all FP specifications (DSI+ classified as likely valid, manual inspection classified as spurious specification) based on the return values of `a()` and `b()`.
- `return-values-spurious-specs.csv` contains the categorization of all specifications manually classified as spurious based on the return values of `a()` and `b()`.
- `return-values-true-specs.csv` contains the categorization of all specifications manually classified as true based on the return values of `a()` and `b()`.
- `specs-mined-at-different-granularities-all-projects.csv` contains the categorization of mined specifications by all 36 projects, based on which granularity level(s) mined each specification.
- `specs-mined-at-different-granularities-all-projects.png` is a graphical view of the total mined specifications based on granularity level(s) by all 36 projects. This is Figure 6[right] in the paper.
- `specs-mined-at-different-granularities-inspected-projects.csv` contains the categorization of mined specifications by the six inspected projects, based on which granularity level(s) mined each specification.
- `specs-mined-at-different-granularities-inspected-projects.png` is a graphical view of the total mined specifications based on granularity level(s) by the six inspected projects. This is Figure 6[left] in the paper.

In `specs-mined-at-different-granularities-*.csv`, `onlyAT`/`onlyTC`/`onlyTM` are the number of specifications that were only mined from the `All Tests`/`Test Classes`/`Test Methods` levels respectively; `AT^TC` is the number of specifications that were mined from both `All Tests` and `Test Classes` levels; `AT^TM` is the number of specifications that were mined from both `All Tests` and `Test Methods` levels; `TC^TM` is the number of specifications that were mined from both `Test Classes` and `Test Methods` levels; `AT^TC^TM` is the number of specifications that were mined at all three levels.


