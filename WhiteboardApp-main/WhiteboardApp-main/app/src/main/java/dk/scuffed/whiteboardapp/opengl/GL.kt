package dk.scuffed.whiteboardapp.opengl

import android.opengl.GLES20
import android.opengl.GLES30

import android.util.Log
import android.util.Size
import dk.scuffed.whiteboardapp.BuildConfig
import dk.scuffed.whiteboardapp.utils.Color
import dk.scuffed.whiteboardapp.utils.Vec2Int
import java.nio.Buffer
import java.nio.ByteBuffer
import java.security.InvalidParameterException


/**
 * Load a shader with source code
 * @param shaderType Specifies the type of shader to be created. Must be either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER.
 * @param shaderCode Specifies the source code of the shader.
 * @return A handle to the compiled shader.
 * @see glCreateShader
 * @see glShaderSource
 * @see glCompileShader
 */
fun loadShader(shaderType: Int, shaderCode: String): Int {
    val shader = glCreateShader(shaderType)
    glShaderSource(shader, shaderCode)
    glCompileShader(shader)
    return shader
}

/**
 * enable server-side GL capabilities
 * @param cap Specifies a symbolic constant indicating a GL capability.
 * @see <a href="https://docs.gl/es2/glEnable">docs.gl - glEnable</a>
 * @see glDisable
 */
fun glEnable(cap: Int) {
    GLES20.glEnable(cap)
    logErrorIfAny("glEnable")
}

/**
 * disable server-side GL capabilities
 * @param cap Specifies a symbolic constant indicating a GL capability.
 * @see <a href="https://docs.gl/es2/glEnable">docs.gl - glEnable</a>
 * @see glEnable
 */
fun glDisable(cap: Int) {
    GLES20.glDisable(cap)
    logErrorIfAny("glDisable")
}

/**
 * clear buffers to preset values
 * @param mask Bitwise OR of masks that indicate the buffers to be cleared.
 * The three masks are GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT, and GL_STENCIL_BUFFER_BIT.
 * @see <a href="https://docs.gl/es2/glClear">docs.gl - glClear</a>
 * @see glClearColor
 */
fun glClear(mask: Int) {
    GLES20.glClear(mask)
    logErrorIfAny("glClear")
}

/**
 * specify clear values for the color buffers
 * @param color Specify the red, green, blue, and alpha values used when the color buffers are cleared. The initial values are all 0.
 * @see <a href="https://docs.gl/es2/glClearColor">docs.gl - glClearColor</a>
 * @see glClear
 */
fun glClearColor(color: Color) {
    GLES20.glClearColor(color.r, color.g, color.b, color.a)
    logErrorIfAny("glClearColor")
}

/**
 * Set the clear color to be transparent
 * @see glClearColorError
 * @see glClearColor
 */
fun glClearColorClear() {
    glClearColor(Color(0f, 0f, 0f, 0f))
}

/**
 * Set the clear color to be magenta
 * @see glClearColorClear
 * @see glClearColor
 */
fun glClearColorError() {
    glClearColor(Color(1f, 0f, 1f, 1f))
}

/**
 * set the viewport
 * @param p The point that specifies the lower left corner of the viewport rectangle in pixels.
 * @param size The width and height of the viewport in pixels
 * @see <a href="https://docs.gl/es2/glViewport">docs.gl - glViewport</a>
 */
fun glViewport(p: Vec2Int, size: Size) {
    GLES20.glViewport(p.x, p.y, size.width, size.height)
    logErrorIfAny("glViewport")
}

/**
 * create a shader object
 * @param shaderType Specifies the type of shader to be created. Must be either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER.
 * @return The handle to the shader.
 * @see <a href="https://docs.gl/es2/glCreateShader">docs.gl - glCreateShader</a>
 * @see glShaderSource
 * @see glCompileShader
 * @see glAttachShader
 */
fun glCreateShader(shaderType: Int): Int {
    val shader = GLES20.glCreateShader(shaderType)
    logErrorIfAny("glCreateShader")
    return shader
}

/**
 * replace the source code in a shader object
 * @param shader Specifies the handle of the shader object whose source code is to be replaced.
 * @param shaderCode The source code for the shader.
 * @see <a href="https://docs.gl/es2/glShaderSource">docs.gl - glShaderSource</a>
 * @see glCreateShader
 * @see glCompileShader
 * @see glAttachShader
 */
fun glShaderSource(shader: Int, shaderCode: String) {
    GLES20.glShaderSource(shader, shaderCode)
    logErrorIfAny("glShaderSource")
}

/**
 * compile a shader object
 * @param shader Specifies the shader object to be compiled.
 * @throws Exception If the shader could not be compiled. This exception contains the error message.
 * @see <a href="https://docs.gl/es2/glCompileShader">docs.gl - glCompileShader</a>
 * @see glCreateShader
 * @see glShaderSource
 * @see glAttachShader
 */
fun glCompileShader(shader: Int) {
    GLES20.glCompileShader(shader)
    logErrorIfAny("glCompileShader")

    val compiled = IntArray(1)
    GLES20.glGetShaderiv(
        shader,
        GLES20.GL_COMPILE_STATUS,
        compiled,
        0
    )

    if (compiled[0] == GLES20.GL_FALSE) {
        throw Exception(GLES20.glGetShaderInfoLog(shader))
    }
}

/**
 * create a program object
 * @return The handle to the handle.
 * @see <a href="https://docs.gl/es2/glCreateProgram">docs.gl - glCreateProgram</a>
 * @see glLinkProgram
 * @see glUseProgram
 */
fun glCreateProgram(): Int {
    val program = GLES20.glCreateProgram()
    logErrorIfAny("glCreateProgram")
    return program
}

/**
 * attach a shader object to a program object
 * @param program Specifies the program object to which a shader object will be attached.
 * @param shader Specifies the shader object that is to be attached.
 * @see <a href="https://docs.gl/es2/glAttachShader">docs.gl - glAttachShader</a>
 * @see glCreateShader
 * @see glShaderSource
 * @see glCompileShader
 * @see glCreateProgram
 */
fun glAttachShader(program: Int, shader: Int) {
    GLES20.glAttachShader(program, shader)
    logErrorIfAny("glAttachShader")
}

/**
 * link a program object
 * @param program Specifies the handle of the program object to be linked.
 * @throws Exception If the program could not be linked. This exception contains the error message.
 * @see <a href="https://docs.gl/es2/glLinkProgram">docs.gl - glLinkProgram</a>
 * @see glCreateProgram
 * @see glUseProgram
 */
fun glLinkProgram(program: Int) {
    GLES20.glLinkProgram(program)
    logErrorIfAny("glLinkProgram")

    val linkStatus = IntArray(1)
    GLES20.glGetProgramiv(
        program,
        GLES20
            .GL_LINK_STATUS,
        linkStatus,
        0
    )

    if (linkStatus[0] == GLES20.GL_FALSE) {
        throw Exception(GLES20.glGetProgramInfoLog(program))
    }
}

/**
 * install a program object as part of current rendering state
 * @param program Specifies the handle of the program object whose executables are to be used as part of current rendering state.
 * @see <a href="https://docs.gl/es2/glUseProgram">docs.gl - glUseProgram</a>
 * @see glCreateProgram
 * @see glLinkProgram
 */
fun glUseProgram(program: Int) {
    GLES20.glUseProgram(program)
    logErrorIfAny("glUseProgram")
}

/**
 * return the location of an attribute variable
 * @param program Specifies the program object to be queried.
 * @param name The name of the attribute variable whose location is to be queried.
 * @return The location of the attribute variable.
 * @see <a href="https://docs.gl/es2/glGetAttribLocation">docs.gl - glGetAttribLocation</a>
 */
fun glGetAttribLocation(program: Int, name: String): Int {
    val location = GLES20.glGetAttribLocation(program, name)
    logErrorIfAny("glGetAttribLocation")
    return location
}

/**
 * enable a generic vertex attribute array
 * @param index Specifies the index of the generic vertex attribute to be enabled.
 * @see <a href="https://docs.gl/es2/glEnableVertexAttribArray">docs.gl - glEnableVertexAttribArray</a>
 */
fun glEnableVertexAttribArray(index: Int) {
    GLES20.glEnableVertexAttribArray(index)
    logErrorIfAny("glEnableVertexAttribArray")
}

/**
 * disable a generic vertex attribute array
 * @param index Specifies the index of the generic vertex attribute to be disabled.
 * @see <a href="https://docs.gl/es2/glEnableVertexAttribArray">docs.gl - glEnableVertexAttribArray</a>
 */
fun glDisableVertexAttribArray(index: Int) {
    GLES20.glDisableVertexAttribArray(index)
    logErrorIfAny("glDisableVertexAttribArray")
}

/**
 * define an array of generic vertex attribute data
 * @param index Specifies the index of the generic vertex attribute to be modified.
 * @param size Specifies the number of components per generic vertex attribute. Must be 1, 2, 3, or 4. The initial value is 4.
 * @param type Specifies the data type of each component in the array. Symbolic constants GL_BYTE, GL_UNSIGNED_BYTE, GL_SHORT, GL_UNSIGNED_SHORT, GL_FIXED, or GL_FLOAT are accepted. The initial value is GL_FLOAT.
 * @param normalized Specifies whether fixed-point data values should be normalized (GL_TRUE) or converted directly as fixed-point values (GL_FALSE) when they are accessed.
 * @param stride Specifies the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in the array. The initial value is 0.
 * @see <a href="https://docs.gl/es2/glVertexAttribPointer">docs.gl - glVertexAttribPointer</a>
 */
fun glVertexAttribPointer(
    index: Int,
    size: Int,
    type: Int,
    normalized: Boolean,
    stride: Int,
    ptr: Buffer
) {
    GLES20.glVertexAttribPointer(index, size, type, normalized, stride, ptr)
    logErrorIfAny("glVertexAttribPointer")
}

/**
 * return the location of a uniform variable
 * @param program Specifies the program object to be queried.
 * @param name The name of the uniform variable whose location is to be queried.
 * @return The location of a the uniform variable.
 * @see <a href="https://docs.gl/es2/glGetUniformLocation">docs.gl - glGetUniformLocation</a>
 */
fun glGetUniformLocation(program: Int, name: String): Int {
    val location = GLES20.glGetUniformLocation(program, name)
    logErrorIfAny("glGetUniformLocation")
    return location
}

/**
 * render primitives from array data
 * @param mode Specifies what kind of primitives to render. Symbolic constants GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, and GL_TRIANGLES are accepted.
 * @param count Specifies the number of elements to be rendered.
 * @param type Specifies the type of the values in indices. Must be GL_UNSIGNED_BYTE or GL_UNSIGNED_SHORT.
 * @param buffer Specifies a buffer where the indices are stored.
 * @see <a href="https://docs.gl/es2/glDrawElements">docs.gl - glDrawElements</a>
 */
fun glDrawElements(mode: Int, count: Int, type: Int, buffer: Buffer) {
    GLES20.glDrawElements(mode, count, type, buffer)
    logErrorIfAny("glDrawElements")
}

/**
 * generate texture names
 * @param n Specifies the number of texture names to be generated.
 * @param textures Specifies an array in which the generated texture names are stored.
 * @param offset Specifies the offset into the array.
 * @see <a href="https://docs.gl/es2/glGenTextures">docs.gl - glGenTextures</a>
 */
fun glGenTextures(n: Int, textures: IntArray, offset: Int) {
    GLES20.glGenTextures(n, textures, offset)
    logErrorIfAny("glGenTextures")
}

/**
 * generate texture name
 * @return The texture name
 * @see glGenTextures
 */
fun glGenTexture(): Int {
    val textures = intArrayOf(999)
    glGenTextures(textures.size, textures, 0)
    return textures[0]
}

/**
 * bind a named texture to a texturing target
 * @param target Specifies the target of the active texture unit to which the texture is bound. Must be either GL_TEXTURE_2D or GL_TEXTURE_CUBE_MAP.
 * @param texture Specifies the name of a texture.
 * @see <a href="https://docs.gl/es2/glBindTexture">docs.gl - glBindTexture</a>
 */
fun glBindTexture(target: Int, texture: Int) {
    GLES20.glBindTexture(target, texture)
    logErrorIfAny("glBindTexture")
}

/**
 * set texture parameters
 * @param target Specifies the target texture of the active texture unit, which must be either GL_TEXTURE_2D or GL_TEXTURE_CUBE_MAP.
 * @param pname Specifies the symbolic name of a single-valued texture parameter. pname can be one of the following: GL_TEXTURE_MIN_FILTER, GL_TEXTURE_MAG_FILTER, GL_TEXTURE_WRAP_S, or GL_TEXTURE_WRAP_T.
 * @param param Specifies the value of pname.
 * @see <a href="https://docs.gl/es2/glTexParameter">docs.gl - glTextParameter</a>
 */
fun glTexParameteri(target: Int, pname: Int, param: Int) {
    GLES20.glTexParameteri(target, pname, param)
    logErrorIfAny("glTexParameteri")
}

/**
 * select active texture unit
 * @param texture Specifies which texture unit to make active. The number of texture units is implementation dependent, but must be at least 8. texture must be one of GL_TEXTUREi, where i ranges from 0 to (GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS - 1). The initial value is GL_TEXTURE0.
 * @see <a href="https://docs.gl/es2/glActiveTexture">docs.gl - glActiveTexture</a>
 */
fun glActiveTexture(texture: Int) {
    GLES20.glActiveTexture(texture)
    logErrorIfAny("glActiveTexture")
}

/**
 * specify a two-dimensional texture image
 * @param target Specifies the target texture of the active texture unit. Must be GL_TEXTURE_2D, GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X, GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GL_TEXTURE_CUBE_MAP_POSITIVE_Z, or GL_TEXTURE_CUBE_MAP_NEGATIVE_Z.
 * @param level Specifies the level-of-detail number. Level 0 is the base image level. Level n is the nth mipmap reduction image.
 * @param format Specifies the format of the texture. Must be one of the following symbolic constants: GL_ALPHA, GL_LUMINANCE, GL_LUMINANCE_ALPHA, GL_RGB, GL_RGBA.
 * @param size Specifies the size of the texture image.
 * @param type Specifies the data type of the texel data. The following symbolic values are accepted: GL_UNSIGNED_BYTE, GL_UNSIGNED_SHORT_5_6_5, GL_UNSIGNED_SHORT_4_4_4_4, and GL_UNSIGNED_SHORT_5_5_5_1.
 * @param data Specifies the image data. This may be null.
 * @see <a href="https://docs.gl/es2/glTexImage2D">docs.gl - glTexImage2D</a>
 */
fun glTexImage2D(
    target: Int,
    level: Int,
    format: Int,
    size: Size,
    type: Int,
    data: ByteBuffer?
) {
    GLES20.glTexImage2D(target, level, format, size.width, size.height, 0, format, type, data)
    logErrorIfAny("glTexImage2D")
}

/**
 * read a block of pixels from the frame buffer
 * @param p Specify the window coordinates of the first pixel that is read from the frame buffer. This location is the lower left corner of a rectangular block of pixels.
 * @param size Specify the dimensions of the pixel rectangle. width and height of one correspond to a single pixel.
 * @param format Specifies the format of the pixel data. The following symbolic values are accepted: GL_ALPHA, GL_RGB, and GL_RGBA.
 * @param data Returns the pixel data.
 * @see <a href="https://docs.gl/es2/glReadPixels">docs.gl - glReadPixels</a>
 */
fun glReadPixels(
    p: Vec2Int,
    size: Size,
    format: Int,
    data: ByteBuffer
) {
    assert(size.width * size.height * bytesPerPixel(format) <= data.capacity())
    GLES20.glReadPixels(p.x, p.y, size.width, size.height, format, GLES20.GL_UNSIGNED_BYTE, data)
    logErrorIfAny("glReadPixels")
}

/**
 * generate framebuffer object names
 * @param n Specifies the number of framebuffer object names to be generated.
 * @param framebuffers Specifies an array in which the generated framebuffer object names are stored.
 * @param offset The offset into the framebuffers array.
 * @see <a href="https://docs.gl/es2/glGenFramebuffers">docs.gl - glGenFramebuffers</a>
 * @see glGenFramebuffer
 */
fun glGenFramebuffers(n: Int, framebuffers: IntArray, offset: Int) {
    GLES20.glGenFramebuffers(n, framebuffers, offset)
    logErrorIfAny("glGenFramebuffers")
}

/**
 * generate a framebuffer object name
 * @return A framebuffer object name
 * @see glGenFramebuffers
 */
fun glGenFramebuffer(): Int {
    val framebuffers = intArrayOf(999)
    glGenFramebuffers(framebuffers.size, framebuffers, 0)
    return framebuffers[0]
}

/**
 * bind a named framebuffer object
 * @param framebuffer Specifies the name of a framebuffer object.
 * @see <a href="https://docs.gl/es2/glBindFramebuffer">docs.gl - glBindFramebuffer</a>
 */
fun glBindFramebuffer(framebuffer: Int) {
    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer)
    logErrorIfAny("glBindFramebuffer")
}

/**
 * attach a texture image to a framebuffer object
 * @param attachment Specifies the attachment point to which an image from texture should be attached. Must be one of the following symbolic constants: GL_COLOR_ATTACHMENT0, GL_DEPTH_ATTACHMENT, or GL_STENCIL_ATTACHMENT.
 * @param textureTarget Specifies the texture target. Must be one of the following symbolic constants: GL_TEXTURE_2D, GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X, GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GL_TEXTURE_CUBE_MAP_POSITIVE_Z, or GL_TEXTURE_CUBE_MAP_NEGATIVE_Z.
 * @param texture Specifies the texture object whose image is to be attached.
 * @see <a href="https://docs.gl/es2/glFramebufferTexture2D">docs.gl - glFramebufferTexture2D</a>
 */
fun glFramebufferTexture2D(attachment: Int, textureTarget: Int, texture: Int) {
    GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachment, textureTarget, texture, 0)
    logErrorIfAny("glFramebufferTexture")
}

/**
 * block until all GL execution is complete
 * @see <a href="https://docs.gl/es2/glFinish">docs.gl - glFinish</a>
 */
fun glFinish() {
    GLES20.glFinish()
    logErrorIfAny("glFinish")
}

fun glUniform1f(location: Int, value: Float) {
    GLES20.glUniform1f(location, value)
    logErrorIfAny("glUniform1f")
}

fun glUniform1fv(location: Int, count: Int, value: FloatArray, offset: Int) {
    GLES20.glUniform1fv(location, count, value, offset)
    logErrorIfAny("glUniform1fv")
}

fun glUniform1i(location: Int, target: Int) {
    GLES20.glUniform1i(location, target)
    logErrorIfAny("glUniform1i")
}

fun glUniform2f(location: Int, x: Float, y: Float) {
    GLES20.glUniform2f(location, x, y)
    logErrorIfAny("glUniform2f")
}

fun glUniform4f(location: Int, r: Float, g: Float, b: Float, a: Float) {
    GLES20.glUniform4f(location, r, g, b, a)
    logErrorIfAny("glUniform4f")
}

fun glUniform4fv(location: Int, count: Int, buffer: FloatArray, offset: Int) {
    GLES20.glUniform4fv(location, count, buffer, offset)
    logErrorIfAny("glUniform4fv")
}

/* OpenGLES3.0 functionality */
fun glBindBuffer(target: Int, buffer: Int) {
    GLES30.glBindBuffer(target, buffer)
    logErrorIfAny("glBindBuffer")
}

fun glMapBufferRange(target: Int, offset: Int, length: Int, access: Int): Buffer {
    val a = GLES30.glMapBufferRange(
        target,
        offset,
        length,
        access
    )
    logErrorIfAny("glMapBufferRange")
    return a
}

fun glUnmapBuffer(target: Int) {
    GLES30.glUnmapBuffer(target)
    logErrorIfAny("glUnmapBuffer")
}


/**
 * Get how many bytes it takes to represent a pixel given a texture format
 * @param textureFormat The texture format
 * @return The amount of bytes it takes to represent a pixel given textureFormat
 * @throws InvalidParameterException textureFormat is not valid
 */
fun bytesPerPixel(textureFormat: Int): Int {
    return when (textureFormat) {
        GLES20.GL_RGBA -> 4
        GLES20.GL_RGB -> 3
        GLES20.GL_ALPHA -> 1

        else -> throw InvalidParameterException("textureFormat")
    }
}

private fun logErrorIfAny(funcname: String) {
    if (BuildConfig.DEBUG) {
        var error = GLES20.glGetError()
        while (error != 0) {
            Log.e("OpenGL", funcname + ": " + error + ": " + errorToString(error))
            error = GLES20.glGetError()
        }
    }
}

private fun errorToString(error: Int): String {
    return when (error) {
        GLES20.GL_INVALID_ENUM -> "GL_INVALID_ENUM"
        GLES20.GL_INVALID_VALUE -> "GL_INVALID_VALUE"
        GLES20.GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
        GLES20.GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
        GLES20.GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION"
        else -> "UNKNOWN ERROR"
    }
}