#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES sTexture;
const highp vec3 W = vec3(0.2125,0.7154,0.0721);
const vec2 TexSize = vec2(100.0,100.0);
const vec4 bkColor = vec4(0.5,0.5,0.5,1.0);


void main(){
    vec2 tex = vTextureCoord;
    vec2 upleftUV = vec2(tex.x-1.0/TexSize.x,tex.y-1.0/TexSize.y);

    vec4 curColor = texture2D(sTexture,vTextureCoord);
    vec4 upleftColor = texture2D(sTexture,upleftUV);

    vec4 delColor = curColor - upleftColor;

    gl_FragColor = vec4(vec3(dot(delColor.rgb,W)),0.0)+bkColor;
}