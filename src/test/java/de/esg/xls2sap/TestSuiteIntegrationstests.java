package de.esg.xls2sap;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("JUnit Platform Suite Demo")
@SelectPackages("de.esg.xls2sap")
@IncludeClassNamePatterns(".*Test")
@IncludeTags("integration-test")
class TestSuiteIntegrationstests
{

}