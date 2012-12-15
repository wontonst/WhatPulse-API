<?

require_once('WhatPulse.php');

class WhatPulseWeb {

    private $style= <<<'STYLE'
<style type="text/css">
body{
background-color: url('img/bg/btsgrid0.jpg');
}
#title, #main{
    position: relative;
    margin:15px auto;
    background-color: #e8e8e8;
    box-shadow: 0 0 8 #666666;
width:880px;
}
#title{
height:120px;
}
#title h1{
font-size:1.5em;
}
#main{
height-min:400px;
}
</style>
STYLE;

private $id;///<id of the user
private $wp;///<WhatPulse object
public function __construct($id) {
if($id instanceof WhatPulse)
{
$this->wp = $id;
$this->id = $this->wp->id;
}else{
    if(!is_numeric($id)) {
        throw new Exception('Failed to create WhatPulseWeb with invalid ID.');
    }
    $this->id = $id;
    $this->wp = new WhatPulse($id);
}
}

public function make()
{
$out = <<<DOC
<HTML>
<HEAD>
<TITLE>WhatPulse {$this->wp->name}</TITLE>
$this->style
</HEAD>
<BODY>
<div id="title">
<h1>{$this->wp->name}</h1>
Ranked {$this->wp->rank}<br />
{$this->wp->pulses} pulses made<br />
</div>
<div id="main">

</div>
</BODY>
</HTML>

DOC;
/*
        echo $this->xml->Pulses.' pulses (last pulsed '.number_format($this->lastpulseago/3600,2).' hours ago '.date('n/j/y @ g:iA',$this->lastpulse).")\n";
        echo 'Key presses: '.$this->totalkeys."\n";
        echo "\t".$this->kperminute.'/minute'."\n\t".$this->kperhour.'/hour'."\n\t".$this->kperday.'/day'."\n";
        echo 'Mouse clicks: '.$this->totalclicks."\n";
        echo "\t".$this->cperminute.'/minute'."\n\t".$this->cperhour.'/hour'."\n\t".$this->cperday.'/day'."\n";
        echo 'Total miles: '.$this->xml->TotalMiles."\n";
        echo 'Date joined: '.$this->xml->DateJoined.' ('.$this->days.' days)'."\n";
*/


return $out;
}
}
?>