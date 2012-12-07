<?

define('BG','bg/');
define('FONT','fonts/');

function create_image() 
{ 
	$image = imagecreatefrompng(BG.'tree.png');  

    //We are making three colors, white, black and gray 
    $white = ImageColorAllocate($image, 255, 255, 255); 
    $black = ImageColorAllocate($image, 0, 0, 0); 
    $grey = ImageColorAllocate($image, 204, 204, 204); 

//adding username
    imagettftext($image, 20, 0.0, 0,0,$white,FONT.'adventure_subtitles.ttf','wontonst' ); 


    //Output the newly created image in jpeg format 
    ImageJpeg($image,'out.jpg'); 
    
    //Free up resources
    ImageDestroy($image); 

}

create_image();


?>