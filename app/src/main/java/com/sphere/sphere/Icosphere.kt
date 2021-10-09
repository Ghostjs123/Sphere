package com.sphere.sphere

import android.util.Log
import java.nio.*
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*


private const val TAG = "Icosphere"

private val PI = acos(-1.0)
private const val EPSILON = 0.000001f;
private const val RADIUS = 0.65f

private const val CORDS_PER_VERTEX = 3

private fun computeIcosahedronVertices() : MutableList<Float> {
    val hANGLE = (PI / 180 * 72).toFloat()
    val vANGLE = (atan(1.0 / 2)).toFloat()

    var hAngle1 = (-PI / 2 - hANGLE / 2).toFloat()
    var hAngle2 = (-PI / 2).toFloat()

    val vertices = MutableList(12 * 3) { 0f }

    vertices[0] = 0f
    vertices[1] = 0f
    vertices[2] = RADIUS

    for (i in 1..5) {
        val i1 = i * 3
        val i2 = (i + 5) * 3

        val z = RADIUS * sin(vANGLE)
        val xy = RADIUS * cos(vANGLE)

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
    vertices[i1 + 2] = -RADIUS

    return vertices
}


class Icosphere {

    private var vertices: MutableList<Float> = mutableListOf()
    private var normals: MutableList<Float> = mutableListOf()
    private var indices: MutableList<Short> = mutableListOf()
    private var lineIndices: MutableList<Short> = mutableListOf()

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var normalsBuffer: FloatBuffer
    private lateinit var indicesBuffer: ShortBuffer
    private lateinit var lineIndicesBuffer: ShortBuffer

    private var subdivision = 2

    init {
        Log.i(TAG, "Icosphere init() Started")
        buildVertices()
        Log.i(TAG, "Icosphere init() Finished")
    }

    private fun addVertices(v1: MutableList<Float>, v2: MutableList<Float>, v3: MutableList<Float>) {
        vertices.add(v1[0])
        vertices.add(v1[1])
        vertices.add(v1[2])
        vertices.add(v2[0])
        vertices.add(v2[1])
        vertices.add(v2[2])
        vertices.add(v3[0])
        vertices.add(v3[1])
        vertices.add(v3[2])
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
        indices.add(i1.toShort())
        indices.add(i2.toShort())
        indices.add(i3.toShort())
    }

    private fun addInitialLineIndices(index: Int) {
        lineIndices.add(index.toShort())
        lineIndices.add((index + 1).toShort())
        lineIndices.add((index + 3).toShort())
        lineIndices.add((index + 4).toShort())
        lineIndices.add((index + 3).toShort())
        lineIndices.add((index + 5).toShort())
        lineIndices.add((index + 4).toShort())
        lineIndices.add((index + 5).toShort())
        lineIndices.add((index + 9).toShort())
        lineIndices.add((index + 10).toShort())
        lineIndices.add((index + 9).toShort())
        lineIndices.add((index + 11).toShort())
    }

    private fun fetchVertex(tmpVertices: MutableList<Float>, i: Int): MutableList<Float> {
        return tmpVertices.subList(i, i + CORDS_PER_VERTEX)
    }

    private fun computeScaleForLength(v: MutableList<Float>): Float {
        return RADIUS / sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2])
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

    // buildVerticesFlat
    private fun buildVertices() {
        val tmpVertices = computeIcosahedronVertices()
        val normal = floatArrayOf(0f, 0f, 0f)
        var index = 0

        vertices.clear()
        normals.clear()
        indices.clear()
        lineIndices.clear()

        val v0 = fetchVertex(tmpVertices, 0)
        val v11 = fetchVertex(tmpVertices, 11 * 3)

        for (i in 1..5) {
            val v1 = fetchVertex(tmpVertices, i * 3)
            val v2 = if (i < 5) fetchVertex(tmpVertices, (i + 1) * 3) else fetchVertex(tmpVertices, 3)
            val v3 = fetchVertex(tmpVertices, (i + 5) * 3)
            val v4 = if ((i + 5) < 10) fetchVertex(tmpVertices, (i + 6) * 3) else fetchVertex(tmpVertices, 6 * 3)

            computeFaceNormal(v0, v1, v2, normal)
            addVertices(v0, v1, v2)
            addNormal(normal)
            addIndices(index, index + 1, index + 2)

            computeFaceNormal(v1, v3, v2, normal)
            addVertices(v1, v3, v2)
            addNormal(normal)
            addIndices(index + 3, index + 4, index + 5)

            computeFaceNormal(v2, v3, v4, normal)
            addVertices(v2, v3, v4)
            addNormal(normal)
            addIndices(index + 6, index + 7, index + 8)

            computeFaceNormal(v3, v11, v4, normal)
            addVertices(v3, v11, v4)
            addNormal(normal)
            addIndices(index + 9, index + 10, index + 11)

            addInitialLineIndices(index)

//            index += 12
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

            var index = 0
            val indexCount = tmpIndices.size

            for (j in 0 until indexCount step 3) {
                val v1 = fetchVertex(tmpVertices, tmpIndices[j] * 3)
                val v2 = fetchVertex(tmpVertices, tmpIndices[j + 1] * 3)
                val v3 = fetchVertex(tmpVertices, tmpIndices[j + 2] * 3)

                val newV1 = computeHalfVertex(v1, v2)
                val newV2 = computeHalfVertex(v2, v3)
                val newV3 = computeHalfVertex(v1, v3)

                computeFaceNormal(v1, newV1, newV3, normal)
                addVertices(v1, newV1, newV3)
                addNormal(normal)
                addIndices(index, index + 1, index + 2)

                computeFaceNormal(newV1, v2, newV2, normal)
                addVertices(newV1, v2, newV2)
                addNormal(normal)
                addIndices(index + 3, index + 4, index + 5)

                computeFaceNormal(newV1, newV2, newV3, normal)
                addVertices(newV1, newV2, newV3)
                addNormal(normal)
                addIndices(index + 6, index + 7, index + 8)

                computeFaceNormal(newV3, newV2, v3, normal)
                addVertices(newV3, newV2, v3)
                addNormal(normal)
                addIndices(index + 9, index + 10, index + 11)

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

    private fun addSubLineIndices(i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int) {
        lineIndices.add(i1.toShort())
        lineIndices.add(i2.toShort())
        lineIndices.add(i2.toShort())
        lineIndices.add(i6.toShort())
        lineIndices.add(i2.toShort())
        lineIndices.add(i3.toShort())
        lineIndices.add(i2.toShort())
        lineIndices.add(i4.toShort())
        lineIndices.add(i6.toShort())
        lineIndices.add(i4.toShort())
        lineIndices.add(i3.toShort())
        lineIndices.add(i4.toShort())
        lineIndices.add(i4.toShort())
        lineIndices.add(i5.toShort())
    }

    private fun setBuffers() {
        val vbb = ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices.toFloatArray())
        vertexBuffer.position(0)

        var nbb = ByteBuffer.allocateDirect(normals.size * Float.SIZE_BYTES)
        nbb.order(ByteOrder.nativeOrder())
        normalsBuffer = nbb.asFloatBuffer()
        normalsBuffer.put(normals.toFloatArray())
        normalsBuffer.position(0)

        val libb = ByteBuffer.allocateDirect(lineIndices.size * Short.SIZE_BYTES)
        libb.order(ByteOrder.nativeOrder())
        lineIndicesBuffer = libb.asShortBuffer()
        lineIndicesBuffer.put(lineIndices.toShortArray())
        lineIndicesBuffer.position(0)

        val ibb = ByteBuffer.allocateDirect(indices.size * Short.SIZE_BYTES)
        ibb.order(ByteOrder.nativeOrder())
        indicesBuffer = ibb.asShortBuffer()
        indicesBuffer.put(indices.toShortArray())
        indicesBuffer.position(0)


    }

    fun draw(gl: GL10) {

        // =====================================================
        // draw()

        gl.glEnable(GL10.GL_POLYGON_OFFSET_FILL)
        gl.glPolygonOffset(1.0f, 1.0f)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)

        gl.glColor4f(0.2f, 1f, 0.2f, 1f)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalsBuffer)

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_SHORT, indicesBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)

        gl.glDisable(GL10.GL_POLYGON_OFFSET_FILL)

        // =====================================================
        // drawLines()

        val color = floatArrayOf(1.0f, 0.3f, 0.3f, 1f)

        gl.glColor4f(color[0], color[1], color[2], color[3])
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_DIFFUSE, color, 0)

        gl.glDisable(GL10.GL_LIGHTING)
        gl.glDisable(GL10.GL_TEXTURE_2D)
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glDrawElements(GL10.GL_LINES, lineIndices.size, GL10.GL_UNSIGNED_SHORT, lineIndicesBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_TEXTURE_2D)

        // =====================================================
    }
}