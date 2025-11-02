#include "stringUtils.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char* removeFirstChar(char input[]) {
    // Allocate memory for the new string
    char* result = (char*)malloc(strlen(input) * sizeof(char));
    
    // Check if memory allocation was successful
    if (result == NULL) {
        printf("Memory allocation failed!\n");
        return NULL;
    }
    
    // Copy the input string starting from the second character
    strcpy(result, input + 1);
    
    return result;
}

char* removeLastChar(char input[]) {
    // Allocate memory for the new string
    char* result = (char*)malloc(strlen(input) * sizeof(char));
    
    // Check if memory allocation was successful
    if (result == NULL) {
        printf("Memory allocation failed!\n");
        return NULL;
    }
    
    // Copy the input string excluding the last character
    strncpy(result, input, strlen(input) - 1);
    // Add null terminator to the end of the string
    result[strlen(input) - 1] = '\0';
    
    return result;
}

char getLastCharacter(const char *str) {
    if (str == NULL || *str == '\0') {
        // If the string is empty or NULL, return a special character to indicate an error.
        return '\0';
    } else {
        // Return the last character of the string.
        return str[strlen(str) - 1];
    }
}