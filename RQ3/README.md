# RQ3: Runtime Overhead

This directory contains the file `runtime-overheads.csv` contains the data used to construct Table 8, along with additional columns `DSI++_*` that list runtime overhead data for the ineffectual approach DSI++ discussed in Section 7.

The columns in `runtime-overheads.csv` are as follows:
- `numSpecs`: number of specifications
- `numTestConfigs`: number of tests
- `Mining(s)`: time in seconds for mining
- `BaseLine(s)` time in seconds to run all tests without DSI+
- `DSI+_wall_time(s)`: total DSI+ wall clock time in seconds
- `DSI+_wall_time(h)`: total DSI+ wall clock time in hours
- `DSI+_seq_time(s)`: total sequential runtime of DSI+ in seconds
- `DSI+_seq_time(h)`: total sequential runtime of DSI+ in hours
- `DSI+_wall_oh(x)`: overhead of DSI+ wall runtime computed as `DSI+_wall_time(s) ÷ BaseLine(s)`
- `DSI+_seq_oh(x)`: overhead of DSI+ sequential runtime computed as `DSI+_seq_time(s) ÷ BaseLine(s)`
- `DSI++_wall_time(s)`: total DSI++ wall clock time in seconds
- `DSI++_wall_time(h)`: total DSI++ wall clock time in hours
- `DSI++_seq_time(s)`: total sequential runtime of DSI++ in seconds
- `DSI++_seq_time(h)`: total sequential runtime of DSI++ in hours
- `DSI++_wall_oh(x)`: overhead of DSI++ wall runtime computed as `DSI++_wall_time(s) ÷ BaseLine(s)`
- `DSI++_seq_oh(x)`: overhead of DSI++ sequential runtime computed as `DSI++_seq_time(s) ÷ BaseLine(s)`
