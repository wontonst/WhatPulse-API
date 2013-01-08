<?
require_once('WhatPulse.php');
//require_once('WhatPulseWeb.php');

$stats = new WhatPulse('wontonst');
$stats->printStats();

//$stats = new WhatPulse(1321);//invalid ID

//$web = new WhatPulseWeb($stats);
//echo $web->make();

?>