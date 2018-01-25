set ns [new Simulator]
set tracefile [open ns2output.txt w]
$ns trace-all $tracefile
set CR1 [$ns node]
set CR2 [$ns node]
set ER2 [$ns node]
set ER1 [$ns node]
set ER3 [$ns node]
set PC4 [$ns node]
set PC3 [$ns node]
set PC6 [$ns node]
set PC5 [$ns node]
set PC8 [$ns node]
set PC7 [$ns node]
set PC9 [$ns node]
set PC12 [$ns node]
set PC1 [$ns node]
set PC2 [$ns node]
set PC11 [$ns node]
set PC10 [$ns node]

$ns duplex-link $ER3 $PC8 1000Mb 20ms DropTail
$ns duplex-link $ER3 $PC9 1000Mb 20ms DropTail
$ns duplex-link $PC6 $ER2 0.1Mb 20ms DropTail
$ns duplex-link $ER3 $PC10 1000Mb 20ms DropTail
$ns duplex-link $ER3 $PC7 1000Mb 20ms DropTail
$ns duplex-link $PC1 $ER1 0.2Mb 20ms DropTail
$ns duplex-link $PC2 $ER1 0.15Mb 20ms DropTail
$ns duplex-link $PC5 $ER2 0.5Mb 20ms DropTail
$ns duplex-link $ER3 $PC12 1000Mb 20ms DropTail
$ns duplex-link $ER3 $PC11 1000Mb 20ms DropTail
$ns duplex-link $PC4 $ER2 0.1Mb 20ms DropTail
$ns duplex-link $PC3 $ER1 0.1Mb 20ms DropTail

$ns simplex-link $ER1 $CR1 1000Mb 20ms dsRED/edge
$ns simplex-link $CR1 $ER1 1000Mb 20ms dsRED/core
set qER1CR1 [[$ns link $ER1 $CR1] queue]
$qER1CR1 setSchedularMode WRR
$qER1CR1 meanPktSize 1000
$qER1CR1 set numQueues_ 2
$qER1CR1 setNumPrec 3
$qER1CR1 addQueueWeights 0 7.2023081422389215
$qER1CR1 addPolicyEntry [$PC2 id] [$PC8 id] TSW2CM 10 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC8 id] [$PC2 id] TSW2CM 10 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC3 id] [$PC9 id] TSW2CM 10 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC9 id] [$PC3 id] TSW2CM 10 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC1 id] [$PC7 id] TSW2CM 10 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC7 id] [$PC1 id] TSW2CM 10 1141443.1281723145
$qER1CR1 addPolicerEntry TSW2CM 10 11
$qER1CR1 addPHBEntry 10 0 0
$qER1CR1 configQ 0 0 10 20 0.05
$qER1CR1 addPHBEntry 11 0 1
$qER1CR1 configQ 0 1 5 10 0.1
$qER1CR1 addQueueWeights 1 5.785949094767446
$qER1CR1 addPolicyEntry [$PC5 id] [$PC11 id] TSW2CM 20 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC11 id] [$PC5 id] TSW2CM 20 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC4 id] [$PC10 id] TSW2CM 20 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC10 id] [$PC4 id] TSW2CM 20 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC6 id] [$PC12 id] TSW2CM 20 1141443.1281723145
$qER1CR1 addPolicyEntry [$PC12 id] [$PC6 id] TSW2CM 20 1141443.1281723145
$qER1CR1 addPolicerEntry TSW2CM 20 21
$qER1CR1 addPHBEntry 20 1 0
$qER1CR1 configQ 1 0 10 20 0.05
$qER1CR1 addPHBEntry 21 1 1
$qER1CR1 configQ 1 1 5 10 0.1

set qCR1ER1 [[$ns link $CR1 $ER1] queue]
$qCR1ER1 setSchedularMode WRR
$qCR1ER1 meanPktSize 1000
$qCR1ER1 set numQueues_ 2
$qCR1ER1 setNumPrec 3
$qCR1ER1 addQueueWeights 0 7.2023081422389215
$qCR1ER1 addPHBEntry 10 0 0
$qCR1ER1 configQ 0 0 10 20 0.05
$qCR1ER1 addPHBEntry 11 0 1
$qCR1ER1 configQ 0 1 5 10 0.1
$qCR1ER1 addQueueWeights 1 5.785949094767446
$qCR1ER1 addPHBEntry 20 1 0
$qCR1ER1 configQ 1 0 10 20 0.05
$qCR1ER1 addPHBEntry 21 1 1
$qCR1ER1 configQ 1 1 5 10 0.1

$ns simplex-link $ER2 $CR2 1000Mb 20ms dsRED/edge
$ns simplex-link $CR2 $ER2 1000Mb 20ms dsRED/core
set qER2CR2 [[$ns link $ER2 $CR2] queue]
$qER2CR2 setSchedularMode WRR
$qER2CR2 meanPktSize 1000
$qER2CR2 set numQueues_ 2
$qER2CR2 setNumPrec 3
$qER2CR2 addQueueWeights 0 7.2023081422389215
$qER2CR2 addPolicyEntry [$PC2 id] [$PC8 id] TSW2CM 10 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC8 id] [$PC2 id] TSW2CM 10 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC3 id] [$PC9 id] TSW2CM 10 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC9 id] [$PC3 id] TSW2CM 10 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC1 id] [$PC7 id] TSW2CM 10 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC7 id] [$PC1 id] TSW2CM 10 1141443.1281723145
$qER2CR2 addPolicerEntry TSW2CM 10 11
$qER2CR2 addPHBEntry 10 0 0
$qER2CR2 configQ 0 0 10 20 0.05
$qER2CR2 addPHBEntry 11 0 1
$qER2CR2 configQ 0 1 5 10 0.1
$qER2CR2 addQueueWeights 1 5.785949094767446
$qER2CR2 addPolicyEntry [$PC5 id] [$PC11 id] TSW2CM 20 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC11 id] [$PC5 id] TSW2CM 20 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC4 id] [$PC10 id] TSW2CM 20 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC10 id] [$PC4 id] TSW2CM 20 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC6 id] [$PC12 id] TSW2CM 20 1141443.1281723145
$qER2CR2 addPolicyEntry [$PC12 id] [$PC6 id] TSW2CM 20 1141443.1281723145
$qER2CR2 addPolicerEntry TSW2CM 20 21
$qER2CR2 addPHBEntry 20 1 0
$qER2CR2 configQ 1 0 10 20 0.05
$qER2CR2 addPHBEntry 21 1 1
$qER2CR2 configQ 1 1 5 10 0.1

set qCR2ER2 [[$ns link $CR2 $ER2] queue]
$qCR2ER2 setSchedularMode WRR
$qCR2ER2 meanPktSize 1000
$qCR2ER2 set numQueues_ 2
$qCR2ER2 setNumPrec 3
$qCR2ER2 addQueueWeights 0 7.2023081422389215
$qCR2ER2 addPHBEntry 10 0 0
$qCR2ER2 configQ 0 0 10 20 0.05
$qCR2ER2 addPHBEntry 11 0 1
$qCR2ER2 configQ 0 1 5 10 0.1
$qCR2ER2 addQueueWeights 1 5.785949094767446
$qCR2ER2 addPHBEntry 20 1 0
$qCR2ER2 configQ 1 0 10 20 0.05
$qCR2ER2 addPHBEntry 21 1 1
$qCR2ER2 configQ 1 1 5 10 0.1

$ns simplex-link $ER2 $CR1 1000Mb 20ms dsRED/edge
$ns simplex-link $CR1 $ER2 1000Mb 20ms dsRED/core
set qER2CR1 [[$ns link $ER2 $CR1] queue]
$qER2CR1 setSchedularMode WRR
$qER2CR1 meanPktSize 1000
$qER2CR1 set numQueues_ 2
$qER2CR1 setNumPrec 3
$qER2CR1 addQueueWeights 0 7.2023081422389215
$qER2CR1 addPolicyEntry [$PC2 id] [$PC8 id] TSW2CM 10 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC8 id] [$PC2 id] TSW2CM 10 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC3 id] [$PC9 id] TSW2CM 10 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC9 id] [$PC3 id] TSW2CM 10 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC1 id] [$PC7 id] TSW2CM 10 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC7 id] [$PC1 id] TSW2CM 10 1141443.1281723145
$qER2CR1 addPolicerEntry TSW2CM 10 11
$qER2CR1 addPHBEntry 10 0 0
$qER2CR1 configQ 0 0 10 20 0.05
$qER2CR1 addPHBEntry 11 0 1
$qER2CR1 configQ 0 1 5 10 0.1
$qER2CR1 addQueueWeights 1 5.785949094767446
$qER2CR1 addPolicyEntry [$PC5 id] [$PC11 id] TSW2CM 20 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC11 id] [$PC5 id] TSW2CM 20 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC4 id] [$PC10 id] TSW2CM 20 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC10 id] [$PC4 id] TSW2CM 20 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC6 id] [$PC12 id] TSW2CM 20 1141443.1281723145
$qER2CR1 addPolicyEntry [$PC12 id] [$PC6 id] TSW2CM 20 1141443.1281723145
$qER2CR1 addPolicerEntry TSW2CM 20 21
$qER2CR1 addPHBEntry 20 1 0
$qER2CR1 configQ 1 0 10 20 0.05
$qER2CR1 addPHBEntry 21 1 1
$qER2CR1 configQ 1 1 5 10 0.1

set qCR1ER2 [[$ns link $CR1 $ER2] queue]
$qCR1ER2 setSchedularMode WRR
$qCR1ER2 meanPktSize 1000
$qCR1ER2 set numQueues_ 2
$qCR1ER2 setNumPrec 3
$qCR1ER2 addQueueWeights 0 7.2023081422389215
$qCR1ER2 addPHBEntry 10 0 0
$qCR1ER2 configQ 0 0 10 20 0.05
$qCR1ER2 addPHBEntry 11 0 1
$qCR1ER2 configQ 0 1 5 10 0.1
$qCR1ER2 addQueueWeights 1 5.785949094767446
$qCR1ER2 addPHBEntry 20 1 0
$qCR1ER2 configQ 1 0 10 20 0.05
$qCR1ER2 addPHBEntry 21 1 1
$qCR1ER2 configQ 1 1 5 10 0.1

$ns simplex-link $ER1 $CR2 1000Mb 20ms dsRED/edge
$ns simplex-link $CR2 $ER1 1000Mb 20ms dsRED/core
set qER1CR2 [[$ns link $ER1 $CR2] queue]
$qER1CR2 setSchedularMode WRR
$qER1CR2 meanPktSize 1000
$qER1CR2 set numQueues_ 2
$qER1CR2 setNumPrec 3
$qER1CR2 addQueueWeights 0 7.2023081422389215
$qER1CR2 addPolicyEntry [$PC2 id] [$PC8 id] TSW2CM 10 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC8 id] [$PC2 id] TSW2CM 10 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC3 id] [$PC9 id] TSW2CM 10 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC9 id] [$PC3 id] TSW2CM 10 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC1 id] [$PC7 id] TSW2CM 10 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC7 id] [$PC1 id] TSW2CM 10 1141443.1281723145
$qER1CR2 addPolicerEntry TSW2CM 10 11
$qER1CR2 addPHBEntry 10 0 0
$qER1CR2 configQ 0 0 10 20 0.05
$qER1CR2 addPHBEntry 11 0 1
$qER1CR2 configQ 0 1 5 10 0.1
$qER1CR2 addQueueWeights 1 5.785949094767446
$qER1CR2 addPolicyEntry [$PC5 id] [$PC11 id] TSW2CM 20 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC11 id] [$PC5 id] TSW2CM 20 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC4 id] [$PC10 id] TSW2CM 20 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC10 id] [$PC4 id] TSW2CM 20 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC6 id] [$PC12 id] TSW2CM 20 1141443.1281723145
$qER1CR2 addPolicyEntry [$PC12 id] [$PC6 id] TSW2CM 20 1141443.1281723145
$qER1CR2 addPolicerEntry TSW2CM 20 21
$qER1CR2 addPHBEntry 20 1 0
$qER1CR2 configQ 1 0 10 20 0.05
$qER1CR2 addPHBEntry 21 1 1
$qER1CR2 configQ 1 1 5 10 0.1

set qCR2ER1 [[$ns link $CR2 $ER1] queue]
$qCR2ER1 setSchedularMode WRR
$qCR2ER1 meanPktSize 1000
$qCR2ER1 set numQueues_ 2
$qCR2ER1 setNumPrec 3
$qCR2ER1 addQueueWeights 0 7.2023081422389215
$qCR2ER1 addPHBEntry 10 0 0
$qCR2ER1 configQ 0 0 10 20 0.05
$qCR2ER1 addPHBEntry 11 0 1
$qCR2ER1 configQ 0 1 5 10 0.1
$qCR2ER1 addQueueWeights 1 5.785949094767446
$qCR2ER1 addPHBEntry 20 1 0
$qCR2ER1 configQ 1 0 10 20 0.05
$qCR2ER1 addPHBEntry 21 1 1
$qCR2ER1 configQ 1 1 5 10 0.1

$ns simplex-link $ER3 $CR2 0.3Mb 20ms dsRED/edge
$ns simplex-link $CR2 $ER3 0.3Mb 20ms dsRED/core
set qER3CR2 [[$ns link $ER3 $CR2] queue]
$qER3CR2 setSchedularMode WRR
$qER3CR2 meanPktSize 1000
$qER3CR2 set numQueues_ 2
$qER3CR2 setNumPrec 3
$qER3CR2 addQueueWeights 0 7.2023081422389215
$qER3CR2 addPolicyEntry [$PC2 id] [$PC8 id] TSW2CM 10 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC8 id] [$PC2 id] TSW2CM 10 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC3 id] [$PC9 id] TSW2CM 10 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC9 id] [$PC3 id] TSW2CM 10 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC1 id] [$PC7 id] TSW2CM 10 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC7 id] [$PC1 id] TSW2CM 10 1141443.1281723145
$qER3CR2 addPolicerEntry TSW2CM 10 11
$qER3CR2 addPHBEntry 10 0 0
$qER3CR2 configQ 0 0 10 20 0.05
$qER3CR2 addPHBEntry 11 0 1
$qER3CR2 configQ 0 1 5 10 0.1
$qER3CR2 addQueueWeights 1 5.785949094767446
$qER3CR2 addPolicyEntry [$PC5 id] [$PC11 id] TSW2CM 20 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC11 id] [$PC5 id] TSW2CM 20 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC4 id] [$PC10 id] TSW2CM 20 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC10 id] [$PC4 id] TSW2CM 20 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC6 id] [$PC12 id] TSW2CM 20 1141443.1281723145
$qER3CR2 addPolicyEntry [$PC12 id] [$PC6 id] TSW2CM 20 1141443.1281723145
$qER3CR2 addPolicerEntry TSW2CM 20 21
$qER3CR2 addPHBEntry 20 1 0
$qER3CR2 configQ 1 0 10 20 0.05
$qER3CR2 addPHBEntry 21 1 1
$qER3CR2 configQ 1 1 5 10 0.1

set qCR2ER3 [[$ns link $CR2 $ER3] queue]
$qCR2ER3 setSchedularMode WRR
$qCR2ER3 meanPktSize 1000
$qCR2ER3 set numQueues_ 2
$qCR2ER3 setNumPrec 3
$qCR2ER3 addQueueWeights 0 7.2023081422389215
$qCR2ER3 addPHBEntry 10 0 0
$qCR2ER3 configQ 0 0 10 20 0.05
$qCR2ER3 addPHBEntry 11 0 1
$qCR2ER3 configQ 0 1 5 10 0.1
$qCR2ER3 addQueueWeights 1 5.785949094767446
$qCR2ER3 addPHBEntry 20 1 0
$qCR2ER3 configQ 1 0 10 20 0.05
$qCR2ER3 addPHBEntry 21 1 1
$qCR2ER3 configQ 1 1 5 10 0.1

$ns simplex-link $ER3 $CR1 0.6Mb 20ms dsRED/edge
$ns simplex-link $CR1 $ER3 0.6Mb 20ms dsRED/core
set qER3CR1 [[$ns link $ER3 $CR1] queue]
$qER3CR1 setSchedularMode WRR
$qER3CR1 meanPktSize 1000
$qER3CR1 set numQueues_ 2
$qER3CR1 setNumPrec 3
$qER3CR1 addQueueWeights 0 7.2023081422389215
$qER3CR1 addPolicyEntry [$PC2 id] [$PC8 id] TSW2CM 10 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC8 id] [$PC2 id] TSW2CM 10 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC3 id] [$PC9 id] TSW2CM 10 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC9 id] [$PC3 id] TSW2CM 10 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC1 id] [$PC7 id] TSW2CM 10 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC7 id] [$PC1 id] TSW2CM 10 1141443.1281723145
$qER3CR1 addPolicerEntry TSW2CM 10 11
$qER3CR1 addPHBEntry 10 0 0
$qER3CR1 configQ 0 0 10 20 0.05
$qER3CR1 addPHBEntry 11 0 1
$qER3CR1 configQ 0 1 5 10 0.1
$qER3CR1 addQueueWeights 1 5.785949094767446
$qER3CR1 addPolicyEntry [$PC5 id] [$PC11 id] TSW2CM 20 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC11 id] [$PC5 id] TSW2CM 20 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC4 id] [$PC10 id] TSW2CM 20 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC10 id] [$PC4 id] TSW2CM 20 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC6 id] [$PC12 id] TSW2CM 20 1141443.1281723145
$qER3CR1 addPolicyEntry [$PC12 id] [$PC6 id] TSW2CM 20 1141443.1281723145
$qER3CR1 addPolicerEntry TSW2CM 20 21
$qER3CR1 addPHBEntry 20 1 0
$qER3CR1 configQ 1 0 10 20 0.05
$qER3CR1 addPHBEntry 21 1 1
$qER3CR1 configQ 1 1 5 10 0.1

set qCR1ER3 [[$ns link $CR1 $ER3] queue]
$qCR1ER3 setSchedularMode WRR
$qCR1ER3 meanPktSize 1000
$qCR1ER3 set numQueues_ 2
$qCR1ER3 setNumPrec 3
$qCR1ER3 addQueueWeights 0 7.2023081422389215
$qCR1ER3 addPHBEntry 10 0 0
$qCR1ER3 configQ 0 0 10 20 0.05
$qCR1ER3 addPHBEntry 11 0 1
$qCR1ER3 configQ 0 1 5 10 0.1
$qCR1ER3 addQueueWeights 1 5.785949094767446
$qCR1ER3 addPHBEntry 20 1 0
$qCR1ER3 configQ 1 0 10 20 0.05
$qCR1ER3 addPHBEntry 21 1 1
$qCR1ER3 configQ 1 1 5 10 0.1

set ServerClientConnection20_transport [new Agent/TCP]
$ServerClientConnection20_transport set fid_ 1
$ServerClientConnection20_transport set packetSize_ 1000

set ServerClientConnection20_application [new Application/FTP]

set ServerClientConnection20_sink [new Agent/TCPSink]

$ns attach-agent $PC2 $ServerClientConnection20_transport
$ServerClientConnection20_application attach-agent $ServerClientConnection20_transport
$ns attach-agent $PC8 $ServerClientConnection20_sink
$ns connect $ServerClientConnection20_transport $ServerClientConnection20_sink

set ServerClientConnection3_transport [new Agent/TCP]
$ServerClientConnection3_transport set fid_ 2
$ServerClientConnection3_transport set packetSize_ 1000

set ServerClientConnection3_application [new Application/FTP]

set ServerClientConnection3_sink [new Agent/TCPSink]

$ns attach-agent $PC3 $ServerClientConnection3_transport
$ServerClientConnection3_application attach-agent $ServerClientConnection3_transport
$ns attach-agent $PC9 $ServerClientConnection3_sink
$ns connect $ServerClientConnection3_transport $ServerClientConnection3_sink

set ServerClientConnection2_transport [new Agent/TCP]
$ServerClientConnection2_transport set fid_ 3
$ServerClientConnection2_transport set packetSize_ 1000

set ServerClientConnection2_application [new Application/FTP]

set ServerClientConnection2_sink [new Agent/TCPSink]

$ns attach-agent $PC1 $ServerClientConnection2_transport
$ServerClientConnection2_application attach-agent $ServerClientConnection2_transport
$ns attach-agent $PC7 $ServerClientConnection2_sink
$ns connect $ServerClientConnection2_transport $ServerClientConnection2_sink

set ServerClientConnection5_transport [new Agent/TCP]
$ServerClientConnection5_transport set fid_ 4

set ServerClientConnection5_application [new Application/FTP]

set ServerClientConnection5_sink [new Agent/TCPSink]

$ns attach-agent $PC5 $ServerClientConnection5_transport
$ServerClientConnection5_application attach-agent $ServerClientConnection5_transport
$ns attach-agent $PC11 $ServerClientConnection5_sink
$ns connect $ServerClientConnection5_transport $ServerClientConnection5_sink

set ServerClientConnection4_transport [new Agent/TCP]
$ServerClientConnection4_transport set fid_ 5

set ServerClientConnection4_application [new Application/FTP]

set ServerClientConnection4_sink [new Agent/TCPSink]

$ns attach-agent $PC4 $ServerClientConnection4_transport
$ServerClientConnection4_application attach-agent $ServerClientConnection4_transport
$ns attach-agent $PC10 $ServerClientConnection4_sink
$ns connect $ServerClientConnection4_transport $ServerClientConnection4_sink

set ServerClientConnection6_transport [new Agent/TCP]
$ServerClientConnection6_transport set fid_ 6

set ServerClientConnection6_application [new Application/FTP]

set ServerClientConnection6_sink [new Agent/TCPSink]

$ns attach-agent $PC6 $ServerClientConnection6_transport
$ServerClientConnection6_application attach-agent $ServerClientConnection6_transport
$ns attach-agent $PC12 $ServerClientConnection6_sink
$ns connect $ServerClientConnection6_transport $ServerClientConnection6_sink

proc finish {} {
global ns tracefile namfile
$ns flush-trace
close $tracefile
exit 0
}

Node instproc nexthop2link { nexthop } {
	set ns_ [Simulator instance]
	foreach {index link} [$ns_ array get link_] {
		set L [split $index :]
		set src [lindex $L 0]
		if {$src == [$self id]} {
			set dst [lindex $L 1]
			if {$dst == $nexthop} { 
				return $link
			}
		}
	}
	return -1
}

proc addExplicitRoute {node dst via } {
	set link2via [$node nexthop2link [$via node-addr]]
	if {$link2via != -1} {
		$node add-route [$dst node-addr] [$link2via head]
	} else {
		puts "Warning: No link exists between node [$node node-addr] and [$via node-addr]. Explicit route not added."
	}
}

$ns at 0 "addExplicitRoute $CR1 $PC7 $ER3"
$ns at 0 "addExplicitRoute $CR1 $PC11 $ER3"
$ns at 0 "addExplicitRoute $CR2 $PC8 $ER3"
$ns at 0 "addExplicitRoute $CR2 $PC9 $ER3"
$ns at 0 "addExplicitRoute $CR2 $PC10 $ER3"
$ns at 0 "addExplicitRoute $CR2 $PC12 $ER3"
$ns at 0 "addExplicitRoute $ER2 $PC10 $CR2"
$ns at 0 "addExplicitRoute $ER2 $PC11 $CR1"
$ns at 0 "addExplicitRoute $ER2 $PC12 $CR2"
$ns at 0 "addExplicitRoute $ER1 $PC7 $CR1"
$ns at 0 "addExplicitRoute $ER1 $PC8 $CR2"
$ns at 0 "addExplicitRoute $ER1 $PC9 $CR2"

$ns at 1 "$ServerClientConnection20_application start "
$ns at 1 "$ServerClientConnection3_application start "
$ns at 1 "$ServerClientConnection4_application start "
$ns at 1 "$ServerClientConnection5_application start "
$ns at 1 "$ServerClientConnection6_application start "
$ns at 100 "$ServerClientConnection20_application stop "
$ns at 100 "$ServerClientConnection3_application stop "
$ns at 100 "$ServerClientConnection4_application stop "
$ns at 100 "$ServerClientConnection5_application stop "
$ns at 100 "$ServerClientConnection6_application stop "
$ns at 100 "finish"
$ns run
