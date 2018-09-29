#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES sTexture;

void main(){

    vec4 oldColor = texture2D(sTexture,vTextureCoord);
    float or = oldColor.r;
    float og = oldColor.g;
    float ob = oldColor.b;
    float oa = oldColor.a;

    float r = abs(og-ob+og+or)*or/256;
    float g = abs(ob-og+ob+or)*or/256;
    float b = abs(ob-og+ob+or)*og/256;

    vec4 newTex = vec4(r,g,b,oa);
    gl_FragColor = Texture2D(sTexture,newTex);

}