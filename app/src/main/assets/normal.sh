 #extension GL_OES_EGL_image_external : require
 precision highp float;
 varying vec2 vTextureCoord;
            uniform samplerExternalOES uSampler;
            void main(){
            gl_FragColor = texture2D(uSampler,vTextureCoord);
            }