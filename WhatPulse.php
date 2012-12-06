<?
class WhatPulse {
    private $id;//whatpulse id
    private $xml;//xml obtained from whatpulse
    private $totalclicks;
    private $totalkeys;
    private $kperhour;
    private $cperhour;
    private $kperday;
    private $cperday;
    private $hours;
    private $days;

    function __construct($id) {
        $this->id = $id;
        $this->getXML();
    }
    function get() {
//time calculation
        $totaltime = time()-strtotime($this->xml->DateJoined);
        $hours = $totaltime/3600;
        $days = $hours/24;
//keypress calculation
        $totalkeys = $this->xml->TotalKeyCount;
        $kperhour = $totalkeys/$hours;
        $kperday = $totalkeys/$days;
//click calculation
        $totalclicks = $this->xml->TotalMouseClicks;
        $cperhour = $totalclicks/$hours;
        $cperday = $totalclicks/$days;
//formatting
        $this->totalclicks = number_format($totalclicks,2);
        $this->totalkeys = number_format($totalkeys,2);
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
            echo 'Could not open '.$url.$this->id.'. Please check your internet connection';
            return;
        }
        $content = stream_get_contents($f);
        $this->xml = new SimpleXMLElement($content);
    }
    function printStats() {
        $this->get();
        echo 'Account Name: '.$this->xml->AccountName.' (id ' .$this->xml->UserID.' ranked '.$this->xml->Rank.")\n";
        echo $this->xml->Pulses.' pulses (last pulsed '.$this->xml->LastPulse.")\n";
        echo 'Key presses: '.$this->totalkeys."\n";
        echo "\t".$this->kperhour.'/hour'."\n\t".$this->kperday.'/day'."\n";
        echo 'Mouse click: '.$this->totalclicks."\n";
        echo "\t".$this->cperhour.'/hour'."\n\t".$this->cperday.'/day'."\n";
        echo 'Total miles: '.$this->xml->TotalMiles."\n";
        echo 'Date joined: '.$this->xml->DateJoined.' ('.$this->days.' days)'."\n";
    }

}
$stats = new WhatPulse(498686);
$stats->printStats();

?>