<img src='http://xmaker.mx/images/squilla.jpg'>
<p>
<h1>We have moved to <a href='https://bitbucket.org/Fantom_JAC'>Bitbucket</a></h1>
<h1>Overview</h1>
<b>Squilla</b> project is dedicated to creating common reusable API frameworks for Java, especially for Java ME(Embedded)/OSGi.<br>
<p>
Main goal is,<br>
<ul>
<li>USB Standard Class drivers using JSR-080. (HID, MSD, UVC, etc...)</li>
<li>USB Peripheral-side(non-Host) API. USB On-The-Go management.</li>
<li>Device management. Manage USB, ZigBee, Bluetooth Devices.</li>
<li>API for low-level interfaces such as I2C, SPI, UART, JTAG.</li>
<li>All services are worked on OSGi framework.</li>
<li>"Pure Java" Storage management, file system implementation.</li>
</ul>
ZigBee related projects are moved to Jazzkaffe(<a href='http://code.google.com/p/jazzkaffe/'>http://code.google.com/p/jazzkaffe/</a> ).<br>
<h1>Current Projects</h1>
<ul>
<li>squilla-commons: Common embedded API(I2C, SPI, JTAG, EEPROM)</li>
<li>squilla-jsr80: javax.usb OSGi support, with OSGi Device Management. Standard USB class drivers.</li>
<li>squilla-xbee: XBee API.</li>
</ul>
<h1>Obsolete Projects</h1>
<ul>
<li>squilla-zigbee: Moved to Jazzkaffe(<a href='http://code.google.com/p/jazzkaffe/'>http://code.google.com/p/jazzkaffe/</a> )</li>
<li>squilla-net</li>
<li>squilla-net-ri</li>
</ul>
<h1>Hardware?</h1>
Our primary target is <b>aJile System aJ-102/200</b> series which are directly executes JVM bytecodes(a.k.a. Java-Processor).<p>
Alternatively, TI's <b>OMAP3</b> platform.<p>
However, most of codes(except USB) are actually runs on <b>PC</b> although this project is targeting embedded hardware.