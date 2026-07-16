package com.benjamin.parsy.gentu.maven.fixture;

import com.benjamin.parsy.gentu.annotation.Description;
import com.benjamin.parsy.gentu.annotation.Expected;
import com.benjamin.parsy.gentu.annotation.File;
import com.benjamin.parsy.gentu.annotation.Given;
import com.benjamin.parsy.gentu.annotation.TestDescriptor;

public class SampleAnnotatedClass {

    @TestDescriptor(
            testName = "Sample test — should do something",
            description = @Description("Verifies the core behavior"),
            given = @Given(
                    value = {"input A", "", "input B"},
                    files = @File(path = "data.json", inClasspath = true)
            ),
            expected = @Expected({"output A", ""})
    )
    @SuppressWarnings("unused")
    public void annotatedMethod() {
        // test method
    }

    @TestDescriptor(
            testName = "Another test",
            description = @Description("Another description"),
            given = @Given(value = {"input X"}),
            expected = @Expected({"output X"})
    )
    @SuppressWarnings("unused")
    public void anotherAnnotatedMethod() {
        // test method
    }

    @SuppressWarnings("unused")
    public void unannotatedMethod() {
        // test method
    }

}
