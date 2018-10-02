#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES sTexture;

void main(){
    float kernel[9];
    kernel[0] = 1.0; kernel[1] = 2.0; kernel[2] = 1.0;
    kernel[3] = 2.0; kernel[4] = 4.0; kernel[5] = 2.0;
    kernel[6] = 1.0; kernel[7] = 2.0; kernel[8] = 1.0;
    float offset = 0.01;
    int index = 0;
    vec4 color = vec4(0.0);
    int x,y;
    vec4 current;
    for(x = 0;x<3;x++){
        for(y = 0;y<3;y++){
           vec2 temp = vec2(float(x-1)*offset,float(y-1)*offset);
           current = texture2D(sTexture,vTextureCoord+temp);
           color+=current*kernel[index];
           index++;
        }
    }

    color/=16.0;

    gl_FragColor = color;
}