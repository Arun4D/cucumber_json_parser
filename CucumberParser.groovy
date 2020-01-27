import groovy.json.JsonSlurper;
import groovy.json.JsonOutput;
import groovy.time.TimeCategory;

CUCUMBER_FILE_NAME = args[0]
CUCUMBER_FILE_PATH = args[1]
EXECUTION_RESULT_JSON_FILE_NAME = args[2]
TEST_EXECUTION_KEY = args[3]

def jsonSlurper = new JsonSlurper()

def cucumberFile
if (!CUCUMBER_FILE_NAME) {
    throw new Exception("arg[0] : Cucumber file is empty ")
}
if (!CUCUMBER_FILE_PATH) {
    throw new Exception("arg[1] : Cucumber file path is empty ")
}

if (!EXECUTION_RESULT_JSON_FILE_NAME) {
    throw new Exception("arg[2] : Result file name is empty ")
}
if (!TEST_EXECUTION_KEY) {
    throw  new Exception("arg[3] : Test Execution Key is empty ")
}

def cucumberFilePathFolder = new File( CUCUMBER_FILE_PATH )
if (cucumberFilePathFolder.exists()) {
   boolean isCucumberFileExists =  new File(cucumberFilePathFolder, CUCUMBER_FILE_NAME).exists()
    if (isCucumberFileExists) {
        cucumberFile = new File(cucumberFilePathFolder, CUCUMBER_FILE_NAME)
    }
}

def cucumberResultObj = jsonSlurper.parse(cucumberFile)

def result = new Result()
//Test Execution key is input
result.setTestExecutionKey(TEST_EXECUTION_KEY)
def tests = new ArrayList<Test>()

cucumberResultObj.each { arrayValue ->
    arrayValue.each {
        key, value ->
            if (key == "elements") {
                value.each { elementsValue ->
                    def test = new Test()
                    def failedOrSkippedResultStatusCount = 0
                    def durations = []
                    elementsValue.each { elementKey, elementValue ->
                        if (elementKey == "start_timestamp") {
                            test.setStart(elementValue)

                        }
                        if (elementKey == "tags") {
                            elementValue.each {
                                testKeyArray ->
                                    testKeyArray.each {
                                        testKeyId, testKeyValue ->
                                            if (testKeyValue) {
                                                test.setTestKey(testKeyValue.substring(1))
                                            }
                                    }
                            }
                        }
                        if (elementKey == "steps") {
                            elementValue.each {
                                stepsArray ->
                                    stepsArray.each {
                                        resultArrayKey, resultArrayValue ->
                                            if (resultArrayKey == "result") {
                                                resultArrayValue.each {
                                                    resultKey, resultValue ->
                                                        if (resultKey != "passed") {
                                                            failedOrSkippedResultStatusCount++
                                                        }
                                                        if (resultKey == "duration") {
                                                            durations.add(resultValue)
                                                        }
                                                }
                                            }
                                    }
                            }
                        }
                    }

                    if (failedOrSkippedResultStatusCount == 0) {
                        test.setStatus("PASS")
                        test.setComment("Successful execution")
                    } else {
                        test.setStatus("FAIL")
                        test.setComment("Failed execution")
                    }

                    def finishTime = calculateFinishTime(test.getStart(), durations)

                    test.setFinish(finishTime)
                    tests.add(test)
                }
            }
    }

}

def calculateFinishTime(def startTime, def durations) {
    Date date = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS", startTime)
    use(TimeCategory) {
        int seconds
        durations.each { duration ->
            long durationValue = duration as Long
            seconds = seconds + (durationValue / 1_000_000_000)
        }

        date = date + seconds.second
        return date.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    }
}

println(tests)
result.setTests(tests)

class Result {
    def testExecutionKey
    List<Test> tests
}

class Test {
    def testKey
    def start
    def finish
    def comment
    def status
}

print JsonOutput.prettyPrint(JsonOutput.toJson(result))
new File(CUCUMBER_FILE_PATH, EXECUTION_RESULT_JSON_FILE_NAME).write(JsonOutput.toJson(result))
