#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES sTexture;


void main(){

    vec3 gray = vec3(0.5,0.5,0.5);
    float weight[3];
    weight[0] = float(0.4026);
    weight[1] = float(0.2442);
    weight[2] = float(0.0545);
    float offset = 1.0/150.0;
   vec2 uv[5],uh[5];
    vec3 centerColor = texture2D(sTexture,vTextureCoord).rgb;
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

    vec3 high_pass = centerColor-color+gray;

    vec3 cha = vec3(1.0,1.0,1.0);

    vec3 finalColor = (centerColor+high_pass*2.0-cha);
    finalColor.r = clamp(pow(finalColor.r,0.5),0.0,1.0);
    finalColor.g = clamp(pow(finalColor.g,0.7),0.0,1.0);
    finalColor.b = clamp(pow(finalColor.b,0.5),0.0,1.0);
    vec3 rouguang = 2.0*centerColor*finalColor+centerColor*centerColor-2.0*centerColor*centerColor*finalColor;
    float cb = 0.5*finalColor.r*255.0-0.4187*finalColor.g*255.0-0.0813*finalColor.b*255.0+128.0;
    float cr = 0.1687*finalColor.r*255.0-0.3313*finalColor.g*255.0+0.5*finalColor.b*255.0+128.0;
    if((cr>=133.0&&cr<=173.0)||(cb>=77.0&&cb<=127.0)){
        //finalColor+=vec3(0.1,0.20,0.2);
    }
    gl_FragColor = vec4( finalColor,0.5);
    vec3 cc = gl_FragColor.rgb*0.2 +rouguang*0.8;
    gl_FragColor.rgb = cc;

}