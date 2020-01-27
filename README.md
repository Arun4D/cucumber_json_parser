# cucumber_json_parser

This is script used to pull the test execution status from cucumber and generate result json to update the existing test execution

### Input parameters to run the test
````
args 0 - "cucumber.json"        -- Generated cucumber file after finish execution
args 1 - "C:\tools\cucumber"    -- Generated cucumber file 
args 2 - "result.json"          -- Result Json. Used to pass to XRAY APi to update the test execution 
args 3 - "DEMO-1206"            -- TestExecutionKey need to pass to update the existing test execution

````
### Result.json sample output

````

{
    "tests": [
        {
            "start": "2020-01-24T13:50:54.004Z",
            "status": "FAIL",
            "comment": "Failed execution",
            "finish": "2020-01-24T13:51:35.004Z",
            "testKey": "XXXXXXX-491"
        },
        {
            "start": "2020-01-24T13:51:36.121Z",
            "status": "FAIL",
            "comment": "Failed execution",
            "finish": "2020-01-24T14:07:24.121Z",
            "testKey": "AAAAA-624"
        }
    ],
    "testExecutionKey": "DEMO-1206"
}

````
