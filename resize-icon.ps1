Add-Type -AssemblyName System.Drawing

$source = "C:\Users\Lydia mwanza\OneDrive\Pictures\APP LOGO MAIN.jpg"
$projectRoot = "C:\Users\Lydia mwanza\StudioProjects\adoption-and-child-care-main"

# Define mipmap sizes
$sizes = @{
    "mipmap-mdpi"    = 48
    "mipmap-hdpi"    = 72
    "mipmap-xhdpi"   = 96
    "mipmap-xxhdpi"  = 144
    "mipmap-xxxhdpi" = 192
}

# Load source image
$img = [System.Drawing.Image]::FromFile($source)
Write-Host "Source image: $($img.Width)x$($img.Height)"

# Target directories (both app and android/app)
$targetDirs = @(
    "$projectRoot\app\src\main\res",
    "$projectRoot\android\app\src\main\res"
)

foreach ($resDir in $targetDirs) {
    foreach ($folder in $sizes.Keys) {
        $size = $sizes[$folder]
        $outDir = "$resDir\$folder"
        
        if (-not (Test-Path $outDir)) {
            New-Item -ItemType Directory -Path $outDir -Force | Out-Null
        }

        # Create resized bitmap
        $bmp = New-Object System.Drawing.Bitmap($size, $size)
        $g = [System.Drawing.Graphics]::FromImage($bmp)
        $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
        $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
        $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
        $g.DrawImage($img, 0, 0, $size, $size)

        # Save as PNG
        $pngPath = "$outDir\ic_launcher.png"
        $bmp.Save($pngPath, [System.Drawing.Imaging.ImageFormat]::Png)
        
        $roundPngPath = "$outDir\ic_launcher_round.png"
        $bmp.Save($roundPngPath, [System.Drawing.Imaging.ImageFormat]::Png)

        # Also save as JPG (overwrite old ones)
        $jpgEncoder = [System.Drawing.Imaging.ImageCodecInfo]::GetImageEncoders() | Where-Object { $_.MimeType -eq "image/jpeg" }
        $encoderParams = New-Object System.Drawing.Imaging.EncoderParameters(1)
        $encoderParams.Param[0] = New-Object System.Drawing.Imaging.EncoderParameter([System.Drawing.Imaging.Encoder]::Quality, 95L)
        
        $jpgPath = "$outDir\ic_launcher.jpg"
        $bmp.Save($jpgPath, $jpgEncoder, $encoderParams)
        
        $roundJpgPath = "$outDir\ic_launcher_round.jpg"
        $bmp.Save($roundJpgPath, $jpgEncoder, $encoderParams)

        $g.Dispose()
        $bmp.Dispose()

        Write-Host "  -> $folder ($size x $size) done"
    }
}

# Create adaptive icon foreground image (108dp = 432px at xxxhdpi)
$adaptiveSize = 432
$bmp = New-Object System.Drawing.Bitmap($adaptiveSize, $adaptiveSize)
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g.Clear([System.Drawing.Color]::Transparent)
# Draw image centered in the safe zone (inner 66dp of 108dp = 264px at xxxhdpi)
$safeSize = 264
$offset = [int](($adaptiveSize - $safeSize) / 2)
$g.DrawImage($img, $offset, $offset, $safeSize, $safeSize)
$g.Dispose()

foreach ($resDir in $targetDirs) {
    $drawableDir = "$resDir\drawable"
    if (-not (Test-Path $drawableDir)) {
        New-Item -ItemType Directory -Path $drawableDir -Force | Out-Null
    }
    $fgPath = "$drawableDir\ic_launcher_foreground.png"
    $bmp.Save($fgPath, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "  -> Adaptive foreground saved to $drawableDir"
}

$bmp.Dispose()
$img.Dispose()

Write-Host "`nAll icons replaced successfully!"
