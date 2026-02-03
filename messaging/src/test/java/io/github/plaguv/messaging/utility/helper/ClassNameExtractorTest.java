package io.github.plaguv.messaging.utility.helper;

import io.github.plaguv.messaging.utlity.helper.ClassNameExtractor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClassNameExtractorTest {

    @Test
    @DisplayName("Should extract upper lower casing from a class")
    void extractsUpperLowerCasing() {
        Assertions.assertEquals("class_name_extractor_test", ClassNameExtractor.extractUpperLower(ClassNameExtractorTest.class));
        Assertions.assertEquals("class_name_extractor_test", ClassNameExtractor.extractUpperLower(ClassNameExtractorTest.class, '_'));
        Assertions.assertEquals("class-name-extractor-test", ClassNameExtractor.extractUpperLower(ClassNameExtractorTest.class, '-'));
        Assertions.assertEquals("class name extractor test", ClassNameExtractor.extractUpperLower(ClassNameExtractorTest.class, ' '));
    }
}