#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES sTexture;

void main(){
    float weight[3];
    weight[0] = float(0.4026);
    weight[1] = float(0.2442);
    weight[2] = float(0.0545);
    float offset = 0.01;
   vec2 uv[5],uh[5];

   uv[0] = vTextureCoord;
   uv[1] = vTextureCoord+vec2(0.0,float(-1.0*offset));
   uv[2] = vTextureCoord+vec2(0.0,float(-2.0*offset));
   uv[3] = vTextureCoord+vec2(0.0,float(1.0*offset));
   uv[4] = vTextureCoord+vec2(0.0,float(2.0*offset));
   vec3 colorv = texture2D(sTexture,vTextureCoord).rgb*weight[0];
    for(int i = 1;i<3;i++){
        colorv+=texture2D(sTexture,uv[i]).rgb*weight[i];
        colorv+=texture2D(sTexture,uv[5-i]).rgb*weight[i];
   }

    uh[0] = vTextureCoord;
    uh[1] = vTextureCoord+vec2(float(-1.0*offset),0.0);
    uh[2] = vTextureCoord+vec2(float(-2.0*offset),0.0);
    uh[3] = vTextureCoord+vec2(float(1.0*offset),0.0);
    uh[4] = vTextureCoord+vec2(float(2.0*offset),0.0);
    vec3 colorh = texture2D(sTexture,vTextureCoord).rgb*weight[0];
    for(int j = 1;j<3;j++){
        colorh+=texture2D(sTexture,uh[j]).rgb*weight[j];
        colorh+=texture2D(sTexture,uh[5-j]).rgb*weight[j];
    }

    vec3 color = colorh*colorv;

    gl_FragColor = vec4(color,1.0);
}