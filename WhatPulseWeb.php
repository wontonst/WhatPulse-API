<?

require_once('WhatPulse.php');

class WhatPulseWeb {

    private $style= <<<'STYLE'
<style type="text/css">
#main{
    position: relative;
    margin:15px auto;
    background-color: #e8e8e8;
    box-shadow: 0 0 8 #666666;
}
</style>
STYLE;

private $id;///<id of the user
private $wp;///<WhatPulse object
public function __construct($id) {
    if(!is_numeric($id)) {
        throw new Exception('Failed to create WhatPulseWeb with invalid ID '.$id);
    }
    $this->id = $id;
    $this->wp = new WhatPulse($id);
}

public function make()
{
$out = '<HTML><HEAD><TITLE>WhatPulse '.$this->wp->name.'</TITLE>'.$this->style.'</HEAD>';



$out .='</HTML>';

return $out;
}
}
?>