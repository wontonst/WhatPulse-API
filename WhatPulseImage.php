<?

function create_image() 
{ 
    //Let's generate a totally random string using md5 
    $md5 = md5(rand(0,999)); 
    //We don't need a 32 character long string so we trim it down to 5 
    $pass = substr($md5, 10, 5); 

    //Set the image width and height 
    $width = 100; 
    $height = 20;  

    //Create the image resource 
    $image = imagecreate($width, $height);  

    //We are making three colors, white, black and gray 
    $white = ImageColorAllocate($image, 255, 255, 255); 
    $black = ImageColorAllocate($image, 0, 0, 0); 
    $grey = ImageColorAllocate($image, 204, 204, 204); 

    //Make the background black 
    ImageFill($image, 0, 0, $black); 

    //Add randomly generated string in white to the image
    ImageString($image, 3, 30, 3, $pass, $white); 

    //Throw in some lines to make it a little bit harder for any bots to break 
    ImageRectangle($image,0,0,$width-1,$height-1,$grey); 
    imageline($image, 0, $height/2, $width, $height/2, $grey); 
    imageline($image, $width/2, 0, $width/2, $height, $grey); 
 
    //Tell the browser what kind of file is come in 
    header("Content-Type: image/jpeg"); 

    //Output the newly created image in jpeg format 
    ImageJpeg($image,'out.jpg'); 
    
    //Free up resources
    ImageDestroy($image); 

}

create_image();


?>