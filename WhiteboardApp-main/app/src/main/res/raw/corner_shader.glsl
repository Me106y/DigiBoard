#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform vec2 offset;
#define beginFadeOutRadius 0.20
float map(float value, float min1, float max1, float min2, float max2){
   return min2+(value-min1)*(max2-min2)/(max1-min1);
}

void main() {
   vec2 uv = (gl_FragCoord.xy - offset-(resolution/2.0))/(resolution);
   float distance = uv.x*uv.x+uv.y*uv.y;

   if(distance>beginFadeOutRadius){
      if (distance <= 0.25) {
         float alpha = map(distance, beginFadeOutRadius, 0.25, 1.0, 0.0);
         gl_FragColor = vec4(0.0, 1.0, 0.0, alpha);
      }
      else {
         gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
      }
   }
   else {
      gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);
   }


}
