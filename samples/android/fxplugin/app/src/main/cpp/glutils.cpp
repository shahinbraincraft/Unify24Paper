//================================================================================
//
// (c) Copyright China Digital Video (Beijing) Limited, 2020. All rights reserved.
//
// This code and information is provided "as is" without warranty of any kind,
// either expressed or implied, including but not limited to the implied
// warranties of merchantability and/or fitness for a particular purpose.
//
//--------------------------------------------------------------------------------
//   Birth Date:    Aug 26. 2020
//   Author:        Meishe video team
//================================================================================
#include "glutils.h"


GLuint CreateShaderProgram(
        const char *vertexShaderSource,
        const char *fragmentShaderSource)
{
    const GLuint vertShader = glCreateShader(GL_VERTEX_SHADER);
    if (!vertShader)
        return 0;

    const GLchar *vertShaderSrc = reinterpret_cast<const GLchar *>(vertexShaderSource);
    glShaderSource(vertShader, 1, &vertShaderSrc, nullptr);
    glCompileShader(vertShader);
    GLint compileStatus = GL_FALSE;
    glGetShaderiv(vertShader, GL_COMPILE_STATUS, &compileStatus);
    if (compileStatus != GL_TRUE) {
        glDeleteShader(vertShader);
        return 0;
    }

    const GLuint fragShader = glCreateShader(GL_FRAGMENT_SHADER);
    if (!fragShader) {
        glDeleteShader(vertShader);
        return 0;
    }

    const GLchar *fragShaderSrc = reinterpret_cast<const GLchar *>(fragmentShaderSource);
    glShaderSource(fragShader, 1, &fragShaderSrc, nullptr);
    glCompileShader(fragShader);
    compileStatus = GL_FALSE;
    glGetShaderiv(fragShader, GL_COMPILE_STATUS, &compileStatus);
    if (compileStatus != GL_TRUE) {
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);
        return 0;
    }

    const GLuint shaderProgram = glCreateProgram();
    if (!shaderProgram) {
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);
        return 0;
    }

    glAttachShader(shaderProgram, vertShader);
    glAttachShader(shaderProgram, fragShader);
    glLinkProgram(shaderProgram);
    glDeleteShader(vertShader);
    glDeleteShader(fragShader);

    GLint linkStatus = GL_FALSE;
    glGetProgramiv(shaderProgram, GL_LINK_STATUS, &linkStatus);
    if (linkStatus != GL_TRUE) {
        glDeleteProgram(shaderProgram);
        return 0;
    }

    return shaderProgram;
}

