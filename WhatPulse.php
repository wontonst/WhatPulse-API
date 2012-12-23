<?
class Stat{
private $data;///<contains total perminute perhour perday
function __construct($tot,&$totaltime,$precision=2)
{
$this->data['total'] = $tot;
$this->calculate($totaltime);
$this->format($precision);
}
function __get($v)
{
return $this->data[$v];
}
private function calculate(&$time)
{
$this->data['perminute'] = $this->total/($time/60);
$this->data['perhour'] = $this->total/($time/3600);
$this->data['perday'] = $this->total/($time/86400);
}
private function format(&$precision)
{
array_walk($this->data,function(&$value,$key) use ($precision){
$value = number_format($value,$precision);
});
}
}
/**
An alternative to whatever the WhatPulse website script offers. To be honest this was written before I discovered that WhatPulse has their own script ready to be deployed. Nonetheless, this code does appear to work and should suffice for most purposes.
@brief retrieves data from the whatpulse web API
@author Roy YiWei Zheng
@version 0.1
*/
class WhatPulse {

    private $id;//whatpulse id
    private $xml;//xml obtained from whatpulse
    private $totalclicks;///<total keyboard actions
    private $totalkeys;///<total mouse clicks
    private $kperminute;///<keyboard actions per minute(string formatted)
    private $cperminute;///<mouse clicks per minute(string formatted)
    private $kperhour;///<keyboard actions per hour(string formatted)
    private $cperhour;///<mouse clicks per hour(string formatted)
    private $kperday;///<keyboard actions per day(string formatted)
    private $cperday;///<mouse clicks per day(string formatted)
    private $minutes;///<user account age in minutes(string formatted)
    private $hours;///<user account age in hours(string formatted)
    private $days;///<user account age in days (string formatted)
private $network;///<user total network operations in megabytes (string formatted)
private $networkratio;///<download:upload ratio
private $download;///<user download in megabytes (string formatted)
private $upload;///<user upload in megabytes (string formatted)
private $uptime;///<user total uptime in hours(string formatted)
    private $lastpulse;///<unix timestamp of last pulse
    private $lastpulseago;///<seconds between now and last pulse
    private $_retrievable = array('id','totalclicks','totalkeys','kperminute','cperminute','kperhour','cperhour','kperday','cperday','minutes','hours','days');///<variables retrievable using magic functions
    private $built;///<whether or not the class has been built

    /**
    @param $id the WhatPulse ID of the user
    @brief constructs the object using the passed ID, simultaneously retrieving the necessary data
    */
    public   function __construct($id) {
        $this->id = $id;
        $this->getXML();
        $this->perform();
    }
    /**
    @brief magic function to retrieve data with $myWhatPulse->totalclicks;
    */
    public function __get($name) {
        switch($name) {
        case 'name':
            return $this->xml->AccountName;
        case 'rank':
            return $this->xml->Rank;
        case 'id':
            return $this->xml->UserID;
        case 'pulses':
            return $this->xml->Pulses;
        }
        if(!in_array($name,$this->_retrievable))
            throw new Exception('Variable '.$name.' does not exist in class WhatPulse.');
        return $this->$name;
    }
    /**
    @brief formats the raw data into a readable string, also performs calculations
    */
    private    function perform() {
//time calculation
        $totaltime = time()-strtotime($this->xml->DateJoined);
        $minutes = $totaltime/60;
        $hours = $minutes/60;
        $days = $hours/24;
//keypress calculation
        $totalkeys = $this->xml->Keys+0.0;
        $kperminute = $totalkeys/$minutes;
        $kperhour = $totalkeys/$hours;
        $kperday = $totalkeys/$days;
//click calculation
        $totalclicks = $this->xml->Clicks+0.0;
        $cperminute = $totalclicks/$minutes;
        $cperhour = $totalclicks/$hours;
        $cperday = $totalclicks/$days;
//click/keypress formatting
        $this->totalclicks = number_format($totalclicks,0);
        $this->totalkeys = number_format($totalkeys,0);
        $this->kperminute = number_format($kperminute,2);
        $this->cperminute = number_format($cperminute,2);
        $this->kperhour = number_format($kperhour,2);
        $this->cperhour = number_format($cperhour,2);
        $this->kperday = number_format($kperday,2);
        $this->cperday = number_format($cperday,2);
        $this->minutes = number_format($minutes,2);
        $this->hours = number_format($hours,2);
        $this->days = number_format($days,2);

//lastpulse
        $temp = date_default_timezone_get();//temporarily store current timezone
        date_default_timezone_set('Europe/Belgrade');//belgrade is where server located
        $datetime = new DateTime($this->xml->LastPulse);//create new DT from belgrade time
        $datetime->setTimezone(new DateTimeZone($temp));//convert belgrade time to current time

        date_default_timezone_set($temp);//reset timezone back to default

        $this->lastpulse = $datetime->getTimestamp();//set lastpulse unix timestamp
        $this->lastpulseago = time()-$this->lastpulse;//get time diff between now and lastpulse
//echo $datetime->format('Y-m-d H:i:s').'::::'.$this->xml->LastPulse;

//network
$this->network = number_format($this->xml->DownloadMB+$this->xml->UploadMB,2);
$this->networkratio = number_format($this->xml->DownloadMB/$this->xml->UploadMB/8,2);
$this->download = new Stat($this->xml->DownloadMB+0.0,$totaltime);
$this->upload = new Stat($this->xml->UploadMB+0.0,$totaltime);

$this->uptime = number_format($this->xml->UptimeSeconds/3600,2);
    }
    /**
    @brief grabs the data from the WhatPulse API, setting the object's SimpleXMLElement
    */
    private function getXML() {
        $url = 'http://whatpulse.org/api/user.php?format=xml&user=';

        $f = fopen($url.$this->id,'r');
        if($f === false) {
            throw new Exception('Could not open '.$url.$this->id.'. Please check your internet connection');
        }
        $content = stream_get_contents($f);
//echo $content;
        $this->xml = new SimpleXMLElement($content);
//var_dump($this->xml);
    }
    /**
    @brief useful for debugging, prints out all class data
    */
    function printStats() {
        echo 'Account Name: '.$this->xml->AccountName.' (id ' .$this->xml->UserID.' ranked '.$this->xml->Rank.")\n";
        echo $this->xml->Pulses.' pulses (last pulsed '.number_format($this->lastpulseago/3600,2).' hours ago '.date('n/j/y @ g:iA',$this->lastpulse).")\n";
        echo 'Key presses: '.$this->totalkeys."\n";
        echo "\t".$this->kperminute.'/minute'."\n\t".$this->kperhour.'/hour'."\n\t".$this->kperday.'/day'."\n";
        echo 'Mouse clicks: '.$this->totalclicks."\n";
        echo "\t".$this->cperminute.'/minute'."\n\t".$this->cperhour.'/hour'."\n\t".$this->cperday.'/day'."\n";
echo 'Total network operations: '.$this->network.' MBytes ('.$this->networkratio.' D/U ratio'.")\n";
echo "\t".$this->download->total.' MBytes downloaded'."\n";
echo "\t\t".$this->download->perminute.'/minute'."\n";
echo "\t\t".$this->download->perhour.'/hour'."\n";
echo "\t\t".$this->download->perday.'/day'."\n";
echo "\t".$this->upload->total.' MBytes uploaded'."\n";
        echo 'Date joined: '.$this->xml->DateJoined.' ('.$this->days.' days)'."\n";

    }

}

?>