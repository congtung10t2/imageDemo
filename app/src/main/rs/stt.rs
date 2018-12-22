#pragma version(1)
#pragma rs java_package_name(com.example.tunghoang)

uint32_t imageWidth;
uint32_t imageHeight;
rs_allocation inputImage;


float4 RS_KERNEL getTextureColorFromXY(rs_allocation input, uint32_t x, uint32_t y){
    float4 g = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(input, x, y));
    return g;
}
uchar4 RS_KERNEL sst(uchar4 in, uint32_t x, uint32_t y){
    float2 scr_size = {imageWidth, imageHeight};
    float3 u = (-1.0f * getTextureColorFromXY(inputImage, x-1, y-1).xyz +
                -2.0f * getTextureColorFromXY(inputImage, x-1, y).xyz +
                -1.0f * getTextureColorFromXY(inputImage, x-1, y+1).xyz +
                1.0f * getTextureColorFromXY(inputImage, x+1, y-1).xyz +
                2.0f * getTextureColorFromXY(inputImage, x+1, y).xyz +
                1.0f * getTextureColorFromXY(inputImage, x+1, y+1).xyz) / 4.0f;


    float3 v = (-1.0f * getTextureColorFromXY(inputImage, x-1, y-1).xyz +
                    -2.0f * getTextureColorFromXY(inputImage, x, y-1).xyz +
                    -1.0f * getTextureColorFromXY(inputImage, x+1, y-1).xyz +
                    1.0f * getTextureColorFromXY(inputImage, x-1, y+1).xyz +
                    2.0f * getTextureColorFromXY(inputImage, x, y+1).xyz +
                    1.0f * getTextureColorFromXY(inputImage, x+1, y+1).xyz) / 4.0f;
     float dotX = dot(u,u);
     float dotV = dot(v,v);
     float dotUV = dot(u,v);
     float4 result = {dotX, dotV, dotUV, 1.0f};
    return rsPackColorTo8888(result);
}


void process(rs_allocation inputImage, rs_allocation outputImage) {
  inputImage = inputImage;
  imageWidth = rsAllocationGetDimX(inputImage);
  imageHeight = rsAllocationGetDimY(inputImage);
 // rsForEach(invert, inputImage, tmp);
  rsForEach(sst, inputImage, outputImage);
}