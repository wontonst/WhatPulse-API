<?

define('BG','bg/');
define('FONT','fonts/');

function create_image() {

$font = FONT.'adventure_subtitles.ttf';

    $image = imagecreatefrompng(BG.'tree.png');

    //We are making three colors, white, black and gray
    $white = ImageColorAllocate($image, 255, 255, 255);
    $black = ImageColorAllocate($image, 0, 0, 0);
    $grey = ImageColorAllocate($image, 204, 204, 204);

//adding username
    imagettftext($image, 40, 0.0, 30,280,$white,$font,'wontonst' );
imagettftext($image,30,0.0,400,277,$white,$font,'ranked 13420');

    ImageJpeg($image,'out.jpg'); //output

    ImageDestroy($image);    //free up resources

}

create_image();


?>