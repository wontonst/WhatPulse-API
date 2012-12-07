<?
class WhatPulse {
    private $id;//whatpulse id
    private $xml;//xml obtained from whatpulse
    private $totalclicks;
    private $totalkeys;
    private $kperminute;
    private $cperminute;
    private $kperhour;
    private $cperhour;
    private $kperday;
    private $cperday;
    private $hours;
    private $days;

private $_retrievable = array('id','totalclicks','totalkeys','kperminute','cperminute','kperhour','cperhour','kperday','cperday','hours','days');
    function __construct($id) {
        $this->id = $id;
        $this->getXML();
    }
function __get($name)
{
if(!in_array($name,$this->_retrievable))
throw new Exception('Variable '.$name.' does not exist in class WhatPulse.');
return $this->$name;
}
    function get() {
//time calculation
        $totaltime = time()-strtotime($this->xml->DateJoined);
        $minutes = $totaltime/60;
        $hours = $minutes/60;
        $days = $hours/24;
//keypress calculation
        $totalkeys = $this->xml->TotalKeyCount+0.0;
        $kperminute = $totalkeys/$minutes;
        $kperhour = $totalkeys/$hours;
        $kperday = $totalkeys/$days;
//click calculation
        $totalclicks = $this->xml->TotalMouseClicks+0.0;
        $cperminute = $totalclicks/$minutes;
        $cperhour = $totalclicks/$hours;
        $cperday = $totalclicks/$days;
//formatting
        $this->totalclicks = number_format($totalclicks,0);
        $this->totalkeys = number_format($totalkeys,0);
        $this->kperminute = number_format($kperminute,2);
        $this->cperminute = number_format($cperminute,2);
        $this->kperhour = number_format($kperhour,2);
        $this->cperhour = number_format($cperhour,2);
        $this->kperday = number_format($kperday,2);
        $this->cperday = number_format($cperday,2);
        $this->hours = number_format($hours,2);
        $this->days = number_format($days,2);
    }
    function getXML() {
        $url = 'http://whatpulse.org/api/user.php?UserID=';

        $f = fopen($url.$this->id,'r');
        if($f === false) {
            throw new Exception('Could not open '.$url.$this->id.'. Please check your internet connection');
        }
        $content = stream_get_contents($f);
        $this->xml = new SimpleXMLElement($content);
    }
    function printStats() {
        $this->get();
        echo 'Account Name: '.$this->xml->AccountName.' (id ' .$this->xml->UserID.' ranked '.$this->xml->Rank.")\n";
        echo $this->xml->Pulses.' pulses (last pulsed '.$this->xml->LastPulse.")\n";
        echo 'Key presses: '.$this->totalkeys."\n";
        echo "\t".$this->kperminute.'/minute'."\n\t".$this->kperhour.'/hour'."\n\t".$this->kperday.'/day'."\n";
        echo 'Mouse click: '.$this->totalclicks."\n";
        echo "\t".$this->cperminute.'/minute'."\n\t".$this->cperhour.'/hour'."\n\t".$this->cperday.'/day'."\n";
        echo 'Total miles: '.$this->xml->TotalMiles."\n";
        echo 'Date joined: '.$this->xml->DateJoined.' ('.$this->days.' days)'."\n";
    }

}

?>