package com.ea.ironmonkey;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

public class SplashScreen {
    private static final String TAG = "SplashScreen";
    private static final int floatSize = 4;
    private Activity _activity;
    private int _attPosition;
    private int _attSampler;
    private int _attTexCoord;
    private int _fragmentShader;
    private int _program;
    private int[] _textureId;
    private int _vertexShader;

    private final String fShaderStr =
                    "precision highp float; " +
                    "varying vec2 v_texCoord;" +
                    "uniform sampler2D s_texture;"+
                    "void main(){" +
                    "   gl_FragColor = texture2D( s_texture, v_texCoord );" +
                    "}";

    private FloatBuffer vBuffer;
    private final String vShaderStr = "attribute vec4 a_position;   \nattribute vec4 a_texCoord;   \nvarying vec2 v_texCoord;     \nvoid main()                  \n{                            \n   gl_Position = a_position; \n   v_texCoord = a_texCoord.xy;  \n}                            \n";

    public SplashScreen(Activity activity) {
        this._activity = activity;
        this._textureId = new int[1];
    }

    private int LoadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        if (glCreateShader == 0) {
            Log.e(TAG, "LoadShader(" + i + ", " + str + " - create shader fail\n");
            return 0;
        }
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateShader;
        }
        Log.e(TAG, "LoadShader(" + i + ", " + str + ") - compile shader fail\n");
        GLES20.glDeleteShader(glCreateShader);
        Log.e(TAG, GLES20.glGetShaderInfoLog(glCreateShader));
        return 0;
    }

    private boolean initRenderer() {
        this._vertexShader = LoadShader(35633, "attribute vec4 a_position;   \nattribute vec4 a_texCoord;   \nvarying vec2 v_texCoord;     \nvoid main()                  \n{                            \n   gl_Position = a_position; \n   v_texCoord = a_texCoord.xy;  \n}                            \n");
        this._fragmentShader = LoadShader(35632, "precision highp float;                              \nvarying vec2 v_texCoord;                            \nuniform sampler2D s_texture;                        \nvoid main()                                         \n{                                                   \n  gl_FragColor = texture2D( s_texture, v_texCoord );\n}                                                   \n");
        this._program = GLES20.glCreateProgram();
        if (this._program == 0 || this._vertexShader == 0 || this._fragmentShader == 0) {
            Log.e(TAG, "InitRender() - fail\n");
            return false;
        }
        GLES20.glAttachShader(this._program, this._vertexShader);
        GLES20.glAttachShader(this._program, this._fragmentShader);
        GLES20.glLinkProgram(this._program);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(this._program, 35714, iArr, 0);
        if (iArr[0] == 0) {
            Log.e(TAG, "InitRender() - fail link program");
            Log.e(TAG, GLES20.glGetProgramInfoLog(this._program));
            GLES20.glDeleteProgram(this._program);
            this._program = 0;
            return false;
        }
        this._attPosition = GLES20.glGetAttribLocation(this._program, "a_position");
        this._attTexCoord = GLES20.glGetAttribLocation(this._program, "a_texCoord");
        this._attSampler = GLES20.glGetAttribLocation(this._program, "s_texture");
        return true;
    }

    public void destroy(GL10 gl10) {
        if (this._textureId[0] != 0) {
            GLES20.glDeleteTextures(1, this._textureId, 0);
            this._textureId[0] = 0;
        }
        if (this._program != 0) {
            GLES20.glDeleteProgram(this._program);
            this._program = 0;
        }
        if (this._vertexShader != 0) {
            GLES20.glDeleteShader(this._vertexShader);
            this._vertexShader = 0;
        }
        if (this._fragmentShader != 0) {
            GLES20.glDeleteShader(this._fragmentShader);
            this._fragmentShader = 0;
        }
        if (this.vBuffer != null) {
            this.vBuffer.clear();
            this.vBuffer = null;
        }
    }

    public boolean draw(GL10 gl10, int i, int i2) {
        float f;
        if (this._textureId[0] == 0) {
            return false;
        }
        float f2 = 0.8f;
        if (i > i2) {
            f = (((float) i2) / ((float) i)) * 0.8f;
        } else {
            f2 = 0.8f * (((float) i) / ((float) i2));
            f = 0.8f;
        }
        float[][] fArr = {new float[]{-f, -f2, 0.0f, 1.0f, f, -f2, 1.0f, 1.0f, -f, f2, 0.0f, 0.0f, f, f2, 1.0f, 0.0f}, new float[]{-f, -f2, 1.0f, 1.0f, f, -f2, 1.0f, 0.0f, -f, f2, 0.0f, 1.0f, f, f2, 0.0f, 0.0f}};
        this.vBuffer = ByteBuffer.allocateDirect(fArr[0].length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        if (i < i2) {
            this.vBuffer.put(fArr[1]);
        } else {
            this.vBuffer.put(fArr[0]);
        }
        GLES20.glViewport(0, 0, i, i2);
        GLES20.glUseProgram(this._program);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this._textureId[0]);
        GLES20.glUniform1i(this._attSampler, 0);
        this.vBuffer.position(0);
        GLES20.glVertexAttribPointer(this._attPosition, 2, 5126, false, 16, (Buffer) this.vBuffer);
        GLES20.glEnableVertexAttribArray(this._attPosition);
        this.vBuffer.position(2);
        GLES20.glVertexAttribPointer(this._attTexCoord, 2, 5126, false, 16, (Buffer) this.vBuffer);
        GLES20.glEnableVertexAttribArray(this._attTexCoord);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glDisableVertexAttribArray(this._attPosition);
        GLES20.glDisableVertexAttribArray(this._attTexCoord);
        GLES20.glUseProgram(0);
        return true;
    }

    public void init(GL10 gl10, int i, int i2) {
        if (!initRenderer()) {
            destroy(gl10);
            return;
        }
        GLES20.glGetError();
        GLES20.glGenTextures(1, this._textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this._textureId[0]);
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this._activity.getAssets().open("splash.png"), null, options);
        } catch (Exception e) {
            Log.e(TAG, "loadBitmap ", e);
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        bitmap.recycle();
        int glGetError = GLES20.glGetError();
        if (glGetError != 0) {
            Log.e(TAG, "Texture Load GLError: " + glGetError);
            destroy(gl10);
        }
    }
}
