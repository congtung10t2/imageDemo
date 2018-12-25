#pragma version(1)
#pragma rs java_package_name(com.example.tunghoang)

static const float3 weight = {0.299f, 0.587f, 0.114f};
uint32_t imageWidth;
uint32_t imageHeight;
uchar4 RS_KERNEL invert(uchar4 in, uint32_t x, uint32_t y) {
  uchar4 out = in;
  out.r = 255 - in.r;
  out.g = 255 - in.g;
  out.b = 255 - in.b;
  return out;
}
rs_allocation tmp;
rs_allocation inputImage;
rs_allocation outputImage;
uchar4 RS_KERNEL tfm(uchar4 in, uint32_t x, uint32_t y){
    //const float4 inF = rsUnpackColor8888(in);
    //float2 uv = {x/imageWidth, y/imageHeight};
    float3 g  = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(inputImage, x, y)).rgb;
    // float3 g = inF.xyz;

   // float3 g = inF.rgb;

    float lambda1 = 0.5f * (g.y + g.x + sqrt(g.y*g.y - 2.0f*g.x*g.y + g.x*g.x + 4.0f*g.z*g.z));
    float lambda2 = 0.5f * (g.y + g.x - sqrt(g.y*g.y - 2.0f*g.x*g.y + g.x*g.x + 4.0f*g.z*g.z));
    float2 v = {lambda1 - g.x, -g.z};
    float2 t;
    if (length(v) > 0.0f) {
        t = normalize(v);
    } else {
        t.x = 0.0f;
        t.y = 1.0f;
    }

    float phi = atan2(t.y, t.x);

    float A = (lambda1 + lambda2 > 0.0f)?(lambda1 - lambda2) / (lambda1 + lambda2) : 0.0f;
    float4 result = {t.x, t.y, phi, A};
    return rsPackColorTo8888(result);
}

float4 RS_KERNEL getTextureColorFromXY(rs_allocation input, uint32_t x, uint32_t y){
    uint32_t trueX = x > imageWidth? imageWidth : (x < 0? 0: x);
    uint32_t trueY = y > imageHeight? imageHeight : (y < 0? 0: y);
    float4 g = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(input, trueX, trueY));
    return g;
}
uchar4 RS_KERNEL sst(uchar4 in, uint32_t x, uint32_t y){
    float2 scr_size = {imageWidth, imageHeight};
    float2 uv = {x/imageWidth, y/imageHeight};
    float2 d = {1.0f/imageWidth, 1.0f/imageHeight};
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
uchar4 RS_KERNEL greyscale(uchar4 in) {
  const float4 inF = rsUnpackColor8888(in);
  const float3 outF = dot(inF.rgb, weight.rgb) ;
  return rsPackColorTo8888(outF);
}

void process(rs_allocation inputImage, rs_allocation outputImage) {
  inputImage = inputImage;
  imageWidth = rsAllocationGetDimX(inputImage);
  imageHeight = rsAllocationGetDimY(inputImage);
  tmp = rsCreateAllocation_uchar4(imageWidth, imageHeight);
 // rsForEach(invert, inputImage, tmp);
  rsForEach(sst, inputImage, outputImage);
}