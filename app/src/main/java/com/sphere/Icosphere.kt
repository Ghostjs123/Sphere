package com.sphere

import android.opengl.GLES20
import android.util.Log
import java.nio.*
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*
import com.sphere.utility.OpenSimplex2F


private const val TAG = "Icosphere"

private val PI = acos(-1.0)
private const val EPSILON = 0.000001f

private const val CORDS_PER_VERTEX = 3

private fun computeIcosahedronVertices(radius: Float) : MutableList<Float> {
    val hANGLE = (PI / 180 * 72).toFloat()
    val vANGLE = (atan(1.0 / 2)).toFloat()

    var hAngle1 = (-PI / 2 - hANGLE / 2).toFloat()
    var hAngle2 = (-PI / 2).toFloat()

    val vertices = MutableList(12 * 3) { 0f }

    vertices[0] = 0f
    vertices[1] = 0f
    vertices[2] = radius

    for (i in 1..5) {
        val i1 = i * 3
        val i2 = (i + 5) * 3

        val z = radius * sin(vANGLE)
        val xy = radius * cos(vANGLE)

        vertices[i1] = xy * cos(hAngle1)
        vertices[i1 + 1] = xy * sin(hAngle1)
        vertices[i1 + 2] = z

        vertices[i2] = xy * cos(hAngle2)
        vertices[i2 + 1] = xy * sin(hAngle2)
        vertices[i2 + 2] = -z

        hAngle1 += hANGLE
        hAngle2 += hANGLE
    }

    val i1 = 11 * 3
    vertices[i1] = 0f
    vertices[i1 + 1] = 0f
    vertices[i1 + 2] = -radius

    return vertices
}


class Icosphere(private val subdivision: Int) {

    companion object {
        const val MIN_RADIUS = 0.3f
        const val MAX_RADIUS = 0.8f
    }

    private var vertices: MutableList<Float> = mutableListOf()
    private var normals: MutableList<Float> = mutableListOf()
    private var indices: MutableList<Int> = mutableListOf()
    private var lineIndices: MutableList<Int> = mutableListOf()
    private var colors: MutableList<Float> = mutableListOf()

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var normalsBuffer: FloatBuffer
    private lateinit var indicesBuffer: IntBuffer
    private lateinit var lineIndicesBuffer: IntBuffer
    private lateinit var colorsBuffer: FloatBuffer

    private var buffersSet = false

    private var radius = 0.65f

    private var defaultColor = mutableListOf(0.3f, 0.3f, 0.3f, 1.0f)

    // ====================================================================
    // Utility Functions

    private fun addVertices(v1: MutableList<Float>, v2: MutableList<Float>, v3: MutableList<Float>) {
        vertices += v1
        vertices += v2
        vertices += v3
    }

    private fun addNormal(normal: FloatArray) {
        normals.add(normal[0])
        normals.add(normal[1])
        normals.add(normal[2])

        normals.add(normal[0])
        normals.add(normal[1])
        normals.add(normal[2])

        normals.add(normal[0])
        normals.add(normal[1])
        normals.add(normal[2])
    }

    private fun addIndices(i1: Int, i2: Int, i3: Int) {
        indices.add(i1)
        indices.add(i2)
        indices.add(i3)
    }

    private fun addInitialLineIndices(index: Int) {
        lineIndices.add(index)
        lineIndices.add(index + 1)
        lineIndices.add(index + 3)
        lineIndices.add(index + 4)
        lineIndices.add(index + 3)
        lineIndices.add(index + 5)
        lineIndices.add(index + 4)
        lineIndices.add(index + 5)
        lineIndices.add(index + 9)
        lineIndices.add(index + 10)
        lineIndices.add(index + 9)
        lineIndices.add(index + 11)
    }

    private fun fetchVertex(tmpVertices: MutableList<Float>, i: Int): MutableList<Float> {
        return tmpVertices.subList(i, i + CORDS_PER_VERTEX)
    }

    private fun computeScaleForLength(v: MutableList<Float>): Float {
        return radius / sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2])
    }

    private fun computeHalfVertex(v1: MutableList<Float>, v2: MutableList<Float>): MutableList<Float> {
        val li = MutableList(3) {0f}

        li[0] = v1[0] + v2[0]
        li[1] = v1[1] + v2[1]
        li[2] = v1[2] + v2[2]
        val scale = computeScaleForLength(li)
        li[0] *= scale
        li[1] *= scale
        li[2] *= scale

        return li
    }

    private fun computeFaceNormal(
        v1: MutableList<Float>,
        v2: MutableList<Float>,
        v3: MutableList<Float>,
        normal: FloatArray
    )
    {
        // default return value
        normal[0] = 0f
        normal[1] = 0f
        normal[2] = 0f

        // find 2 edge vectors: v1-v2, v1-v3
        val ex1 = v2[0] - v1[0]
        val ey1 = v2[1] - v1[1]
        val ez1 = v2[2] - v1[2]
        val ex2 = v3[0] - v1[0]
        val ey2 = v3[1] - v1[1]
        val ez2 = v3[2] - v1[2]

        // cross product: e1 x e2
        val nx = ey1 * ez2 - ez1 * ey2
        val ny = ez1 * ex2 - ex1 * ez2
        val nz = ex1 * ey2 - ey1 * ex2

        // normalize only if the length is > 0
        val length: Float = sqrt(nx * nx + ny * ny + nz * nz)
        if (length > EPSILON) {
            // normalize
            val lengthInv = 1.0f / length
            normal[0] = nx * lengthInv
            normal[1] = ny * lengthInv
            normal[2] = nz * lengthInv
        }
    }

    private fun addSubLineIndices(i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int) {
        lineIndices.add(i1)
        lineIndices.add(i2)
        lineIndices.add(i2)
        lineIndices.add(i6)
        lineIndices.add(i2)
        lineIndices.add(i3)
        lineIndices.add(i2)
        lineIndices.add(i4)
        lineIndices.add(i6)
        lineIndices.add(i4)
        lineIndices.add(i3)
        lineIndices.add(i4)
        lineIndices.add(i4)
        lineIndices.add(i5)
    }

    private fun addDefaultColor() {
        colors += defaultColor
        colors += defaultColor
        colors += defaultColor
    }

    private fun changeColor(i: Int, r: Float, g: Float, b: Float, a: Float = 1.0f) {
        val offset = 12

        colors[i * offset] = r
        colors[(i * offset) + 1] = g
        colors[(i * offset) + 2] = b
        colors[(i * offset) + 3] = a

        colors[(i * offset) + 4] = r
        colors[(i * offset) + 5] = g
        colors[(i * offset) + 6] = b
        colors[(i * offset) + 7] = a

        colors[(i * offset) + 8] = r
        colors[(i * offset) + 9] = g
        colors[(i * offset) + 10] = b
        colors[(i * offset) + 11] = a

        Log.v(TAG, "changeColor " + (i * offset) + " through " + ((i * offset) + 11))
    }

    // ====================================================================
    // Vertices Functions

    // buildVerticesFlat
    fun buildVertices() {
        buffersSet = false

        val tmpVertices = computeIcosahedronVertices(radius)
        val normal = floatArrayOf(0f, 0f, 0f)
        var index = 0

        vertices.clear()
        normals.clear()
        indices.clear()
        lineIndices.clear()
        colors.clear()

        val v0 = fetchVertex(tmpVertices, 0)
        val v11 = fetchVertex(tmpVertices, 11 * 3)

        for (i in 1..5) {
            val v1 = fetchVertex(tmpVertices, i * 3)

            val v2 =
                if (i < 5) fetchVertex(tmpVertices, (i + 1) * 3)
                else fetchVertex(tmpVertices, 3)

            val v3 = fetchVertex(tmpVertices, (i + 5) * 3)

            val v4 =
                if ((i + 5) < 10) fetchVertex(tmpVertices, (i + 6) * 3)
                else fetchVertex(tmpVertices, 6 * 3)

            computeFaceNormal(v0, v1, v2, normal)
            addVertices(v0, v1, v2)
            addNormal(normal)
            addIndices(index, index + 1, index + 2)
            addDefaultColor()

            computeFaceNormal(v1, v3, v2, normal)
            addVertices(v1, v3, v2)
            addNormal(normal)
            addIndices(index + 3, index + 4, index + 5)
            addDefaultColor()

            computeFaceNormal(v2, v3, v4, normal)
            addVertices(v2, v3, v4)
            addNormal(normal)
            addIndices(index + 6, index + 7, index + 8)
            addDefaultColor()

            computeFaceNormal(v3, v11, v4, normal)
            addVertices(v3, v11, v4)
            addNormal(normal)
            addIndices(index + 9, index + 10, index + 11)
            addDefaultColor()

            addInitialLineIndices(index)

            index += 12
        }

        subdivideVertices()

        setBuffers()
    }

    // subdivideVerticesFlat
    private fun subdivideVertices() {
        for (i in 1..subdivision) {
            val tmpVertices = vertices.toMutableList()
            val tmpIndices = indices.toMutableList()
            val normal = floatArrayOf(0f, 0f, 0f)

            vertices.clear()
            normals.clear()
            indices.clear()
            lineIndices.clear()
            colors.clear()

            var index = 0
            val indexCount = tmpIndices.size

            for (j in 0 until indexCount step 3) {
                val v1 = fetchVertex(tmpVertices, tmpIndices[j] * 3)
                val v2 = fetchVertex(tmpVertices, tmpIndices[j + 1] * 3)
                val v3 = fetchVertex(tmpVertices, tmpIndices[j + 2] * 3)

                val newV1 = computeHalfVertex(v1, v2)
                val newV2 = computeHalfVertex(v2, v3)
                val newV3 = computeHalfVertex(v1, v3)

                addVertices(v1, newV1, newV3)
                computeFaceNormal(v1, newV1, newV3, normal)
                addNormal(normal)
                addIndices(index, index + 1, index + 2)
                addDefaultColor()

                addVertices(newV1, v2, newV2)
                computeFaceNormal(newV1, v2, newV2, normal)
                addNormal(normal)
                addIndices(index + 3, index + 4, index + 5)
                addDefaultColor()

                addVertices(newV1, newV2, newV3)
                computeFaceNormal(newV1, newV2, newV3, normal)
                addNormal(normal)
                addIndices(index + 6, index + 7, index + 8)
                addDefaultColor()

                addVertices(newV3, newV2, v3)
                computeFaceNormal(newV3, newV2, v3, normal)
                addNormal(normal)
                addIndices(index + 9, index + 10, index + 11)
                addDefaultColor()

                addSubLineIndices(
                    index,
                    index + 1,
                    index + 4,
                    index + 5,
                    index + 11,
                    index + 9
                )

                index += 12
            }
        }
    }

    // ====================================================================
    // Mutate Functions

    private fun transformNoiseToRadius(noiseValue: Double): Float{
        // linear interpolation
        val oldMin = -1.0f; val oldMax = 1.0f
        val newMin = MIN_RADIUS; val newMax = MAX_RADIUS
        return (noiseValue.toFloat() - oldMin) * (newMax - newMin) / (oldMax - oldMin) + newMin
    }

    private fun transformRadiusToColor(radius: Float): Float {
        // linear interpolation
        val oldMin = MIN_RADIUS; val oldMax = MAX_RADIUS
        val newMin = 0.2f; val newMax = 0.7f
        return (radius - oldMin) * (newMax - newMin) / (oldMax - oldMin) + newMin
    }

    private fun mutateVertex(i: Int, noise: OpenSimplex2F, seed: Long?): MutableList<Float> {
        val vertex = fetchVertex(vertices, i)

        val x = vertex[0]
        val y = vertex[1]
        val z = vertex[2]

        val r = sqrt(x * x + y * y + z * z)
        val theta = atan2(y, x)
        val rho = atan2(sqrt(x * x + y * y), z)

        val noiseMode = seed?.rem(10)?.toInt()?.let { abs(it) }
        var newR = 0f

        if (noiseMode == 0 || noiseMode == 1 || noiseMode == 2 || noiseMode == 9) {
            newR = transformNoiseToRadius(
                noise.noise3_Classic(
                    x.toDouble(),
                    y.toDouble(),
                    z.toDouble()
                )
            )
        } else if (noiseMode == 3) {
            newR = transformNoiseToRadius(
                noise.noise3_Classic(
                    r.toDouble(),
                    theta.toDouble(),
                    rho.toDouble()
                )
            )
        } else if (noiseMode == 4) {
            newR = transformNoiseToRadius(
                noise.noise3_XZBeforeY(
                    x.toDouble(),
                    y.toDouble(),
                    z.toDouble()
                )
            )
        } else if (noiseMode == 5) {
            newR = transformNoiseToRadius(
                noise.noise2(
                    (x * y * z).toDouble(),
                    (r * rho * theta).toDouble()
                )
            )
        } else if (noiseMode == 6 || noiseMode == 7 || noiseMode == 8) {
            newR = transformNoiseToRadius(
                noise.noise2(
                    theta.toDouble(),
                    rho.toDouble()
                )
            )
        }

        vertices[i] = newR * sin(rho) * cos(theta)
        vertices[i + 1] = newR * sin(rho) * sin(theta)
        vertices[i + 2] = newR * cos(rho)

//        val scl = transformRadiusToColor(newR)
        colors.add(0.1f)
        colors.add(0.5f)
        colors.add(0.1f)
        colors.add(1.0f)

        return mutableListOf(vertices[i], vertices[i + 1], vertices[i + 2])
    }

    fun mutate(seed: Long?) {
        Log.i(TAG, "Beginning Sphere Mutation")

        buildVertices()

        if (seed == null) return

        val noise = OpenSimplex2F(seed)
        val normal = floatArrayOf(0f, 0f, 0f)
        val count = indices.size

        normals.clear()
        colors.clear()

        for (i in 0 until count step 3) {
            val v1 = mutateVertex(indices[i] * 3, noise, seed)
            val v2 = mutateVertex(indices[i + 1] * 3, noise, seed)
            val v3 = mutateVertex(indices[i + 2] * 3, noise, seed)

            computeFaceNormal(v1, v2, v3, normal)
            addNormal(normal)
        }

        setBuffers()

        Log.i(TAG, "Done Mutating Sphere")
    }

    // ====================================================================
    // Buffer Functions

    private fun setBuffers() {
        val vbb = ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices.toFloatArray())
        vertexBuffer.position(0)

        val nbb = ByteBuffer.allocateDirect(normals.size * Float.SIZE_BYTES)
        nbb.order(ByteOrder.nativeOrder())
        normalsBuffer = nbb.asFloatBuffer()
        normalsBuffer.put(normals.toFloatArray())
        normalsBuffer.position(0)

        val libb = ByteBuffer.allocateDirect(lineIndices.size * Int.SIZE_BYTES)
        libb.order(ByteOrder.nativeOrder())
        lineIndicesBuffer = libb.asIntBuffer()
        lineIndicesBuffer.put(lineIndices.toIntArray())
        lineIndicesBuffer.position(0)

        val ibb = ByteBuffer.allocateDirect(indices.size * Int.SIZE_BYTES)
        ibb.order(ByteOrder.nativeOrder())
        indicesBuffer = ibb.asIntBuffer()
        indicesBuffer.put(indices.toIntArray())
        indicesBuffer.position(0)

        val cbb = ByteBuffer.allocateDirect(colors.size * Float.SIZE_BYTES)
        cbb.order(ByteOrder.nativeOrder())
        colorsBuffer = cbb.asFloatBuffer()
        colorsBuffer.put(colors.toFloatArray())
        colorsBuffer.position(0)

        buffersSet = true
    }

    // ====================================================================
    // Draw Function

    fun draw(gl: GL10) {
        if (!buffersSet) return  // NOTE: this is to avoid concurrency issues

        // =====================================================
        // draw()

        gl.glEnable(GL10.GL_POLYGON_OFFSET_FILL)
        gl.glPolygonOffset(1.0f, 1.0f)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalsBuffer)
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorsBuffer)

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_INT, indicesBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY)

        gl.glDisable(GL10.GL_POLYGON_OFFSET_FILL)

        // =====================================================
        // drawLines()

        gl.glDisable(GL10.GL_LIGHTING)
        gl.glDisable(GL10.GL_TEXTURE_2D)

//        val color = floatArrayOf(0.3f, 0.3f, 0.3f, 1f)
//
//        gl.glColor4f(color[0], color[1], color[2], color[3])
//        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_DIFFUSE, color, 0)
//
//        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
//
//        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
//        gl.glDrawElements(GL10.GL_LINES, lineIndices.size, GLES20.GL_UNSIGNED_INT, lineIndicesBuffer)
//
//        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)

        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_TEXTURE_2D)

        // =====================================================
    }
}