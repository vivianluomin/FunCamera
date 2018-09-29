#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES sTexture;


void main(){

    vec4 minValue = vec4(1.0);
    int coreSize = 3;
    int halfSize = coreSize/2;
    float offset = 1.0/600.0;
    int y,x;
    for( y = 0;y<coreSize;y++){
       for( x = 0;x<coreSize;x++){
            vec2 temp = vec2(float(x-halfSize)*offset,float(y-halfSize)*offset);
            vec4 currentAlpha = texture2D(sTexture,vTextureCoord+temp);
            minValue = min(minValue,currentAlpha);
        }
    }

    gl_FragColor = minValue;

}