#version 330

in vec2 TexCoord0;
out vec4 FragColor;
uniform sampler2D sample;

void main(){
    FragColor = texture2D(sample, TexCoord0.st);
}