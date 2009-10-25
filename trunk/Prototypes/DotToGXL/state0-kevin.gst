<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
	<graph id="state0-kevin" role="graph" edgeids="false" edgemode="directed">
		<attr name="$version">
			<string>curly</string>
		</attr>
		<node id="n1"/>
		<edge from="n1" to="n1">
			<attr name="label">
				<string>Food</string>
			</attr>
		</edge>
		<node id="n2"/>
		<edge from="n2" to="n2">
			<attr name="label">
				<string>Class</string>
			</attr>
		</edge>
		<edge from="n1" to="n2">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n3"/>
		<edge from="n3" to="n3">
			<attr name="label">
				<string>foodSM</string>
			</attr>
		</edge>
		<edge from="n1" to="n3">
			<attr name="label">
				<string>classifierBehavior</string>
			</attr>
		</edge>
		<node id="n4"/>
		<edge from="n4" to="n4">
			<attr name="label">
				<string>temperature</string>
			</attr>
		</edge>
		<edge from="n1" to="n4">
			<attr name="label">
				<string>ownedAttribute</string>
			</attr>
		</edge>
		<node id="n5"/>
		<edge from="n5" to="n5">
			<attr name="label">
				<string>Property</string>
			</attr>
		</edge>
		<edge from="n4" to="n5">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n6"/>
		<edge from="n6" to="n6">
			<attr name="label">
				<string>Integer</string>
			</attr>
		</edge>
		<edge from="n4" to="n6">
			<attr name="label">
				<string>type</string>
			</attr>
		</edge>
		<node id="n7"/>
		<edge from="n7" to="n7">
			<attr name="label">
				<string>PrimitiveType</string>
			</attr>
		</edge>
		<edge from="n6" to="n7">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n1" to="n3">
			<attr name="label">
				<string>ownedBehavior</string>
			</attr>
		</edge>
		<node id="n8"/>
		<edge from="n8" to="n8">
			<attr name="label">
				<string>StateMachine</string>
			</attr>
		</edge>
		<edge from="n3" to="n8">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n9"/>
		<edge from="n9" to="n9">
			<attr name="label">
				<string>foodSMregion</string>
			</attr>
		</edge>
		<edge from="n3" to="n9">
			<attr name="label">
				<string>region</string>
			</attr>
		</edge>
		<node id="n10"/>
		<edge from="n10" to="n10">
			<attr name="label">
				<string>Region</string>
			</attr>
		</edge>
		<edge from="n9" to="n10">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n11"/>
		<edge from="n11" to="n11">
			<attr name="label">
				<string>foodInitial</string>
			</attr>
		</edge>
		<edge from="n9" to="n11">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<node id="n12"/>
		<edge from="n12" to="n12">
			<attr name="label">
				<string>Pseudostate</string>
			</attr>
		</edge>
		<edge from="n11" to="n12">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n13"/>
		<edge from="n13" to="n13">
			<attr name="label">
				<string>foodFinal</string>
			</attr>
		</edge>
		<edge from="n9" to="n13">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<node id="n14"/>
		<edge from="n14" to="n14">
			<attr name="label">
				<string>FinalState</string>
			</attr>
		</edge>
		<edge from="n13" to="n14">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n15"/>
		<edge from="n15" to="n15">
			<attr name="label">
				<string>NotCooked</string>
			</attr>
		</edge>
		<edge from="n9" to="n15">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<node id="n16"/>
		<edge from="n16" to="n16">
			<attr name="label">
				<string>State</string>
			</attr>
		</edge>
		<edge from="n15" to="n16">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n17"/>
		<edge from="n17" to="n17">
			<attr name="label">
				<string>Cooked</string>
			</attr>
		</edge>
		<edge from="n9" to="n17">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n17" to="n16">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n18"/>
		<edge from="n18" to="n18">
			<attr name="label">
				<string>foodInitialTrans</string>
			</attr>
		</edge>
		<edge from="n9" to="n18">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<node id="n19"/>
		<edge from="n19" to="n19">
			<attr name="label">
				<string>Transition</string>
			</attr>
		</edge>
		<edge from="n18" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n18" to="n15">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n18" to="n11">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n20"/>
		<edge from="n20" to="n20">
			<attr name="label">
				<string>foodFinalTrans</string>
			</attr>
		</edge>
		<edge from="n9" to="n20">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n20" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n20" to="n13">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n20" to="n17">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n21"/>
		<edge from="n21" to="n21">
			<attr name="label">
				<string>cookedTrans</string>
			</attr>
		</edge>
		<edge from="n9" to="n21">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n21" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n21" to="n17">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n21" to="n15">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n22"/>
		<edge from="n22" to="n22">
			<attr name="label">
				<string>cookedTrigger</string>
			</attr>
		</edge>
		<edge from="n21" to="n22">
			<attr name="label">
				<string>trigger</string>
			</attr>
		</edge>
		<node id="n23"/>
		<edge from="n23" to="n23">
			<attr name="label">
				<string>Trigger</string>
			</attr>
		</edge>
		<edge from="n22" to="n23">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n24"/>
		<edge from="n24" to="n24">
			<attr name="label">
				<string>receiveCooked</string>
			</attr>
		</edge>
		<edge from="n22" to="n24">
			<attr name="label">
				<string>event</string>
			</attr>
		</edge>
		<node id="n25"/>
		<edge from="n25" to="n25">
			<attr name="label">
				<string>Microwave</string>
			</attr>
		</edge>
		<edge from="n25" to="n2">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n26"/>
		<edge from="n26" to="n26">
			<attr name="label">
				<string>microwaveSM</string>
			</attr>
		</edge>
		<edge from="n25" to="n26">
			<attr name="label">
				<string>classifierBehavior</string>
			</attr>
		</edge>
		<edge from="n25" to="n26">
			<attr name="label">
				<string>ownedBehavior</string>
			</attr>
		</edge>
		<edge from="n26" to="n8">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n27"/>
		<edge from="n27" to="n27">
			<attr name="label">
				<string>microwaveSMregion</string>
			</attr>
		</edge>
		<edge from="n26" to="n27">
			<attr name="label">
				<string>region</string>
			</attr>
		</edge>
		<edge from="n27" to="n10">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n28"/>
		<edge from="n28" to="n28">
			<attr name="label">
				<string>microwaveInitial</string>
			</attr>
		</edge>
		<edge from="n27" to="n28">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n28" to="n12">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n29"/>
		<edge from="n29" to="n29">
			<attr name="label">
				<string>microwaveFinal</string>
			</attr>
		</edge>
		<edge from="n27" to="n29">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n29" to="n14">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n30"/>
		<edge from="n30" to="n30">
			<attr name="label">
				<string>NotCooking</string>
			</attr>
		</edge>
		<edge from="n27" to="n30">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n30" to="n16">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n31"/>
		<edge from="n31" to="n31">
			<attr name="label">
				<string>Cooking</string>
			</attr>
		</edge>
		<edge from="n27" to="n31">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n31" to="n16">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n32"/>
		<edge from="n32" to="n32">
			<attr name="label">
				<string>CookingDo</string>
			</attr>
		</edge>
		<edge from="n31" to="n32">
			<attr name="label">
				<string>doActivity</string>
			</attr>
		</edge>
		<node id="n33"/>
		<edge from="n33" to="n33">
			<attr name="label">
				<string>Activity</string>
			</attr>
		</edge>
		<edge from="n32" to="n33">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n34"/>
		<edge from="n34" to="n34">
			<attr name="label">
				<string>cookIncrement</string>
			</attr>
		</edge>
		<edge from="n32" to="n34">
			<attr name="label">
				<string>node</string>
			</attr>
		</edge>
		<node id="n35"/>
		<edge from="n35" to="n35">
			<attr name="label">
				<string>AddStructuralFeatureValueAction</string>
			</attr>
		</edge>
		<edge from="n34" to="n35">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n36"/>
		<edge from="n36" to="n36">
			<attr name="label">
				<string>true</string>
			</attr>
		</edge>
		<edge from="n34" to="n36">
			<attr name="label">
				<string>isReplaceAll</string>
			</attr>
		</edge>
		<node id="n37"/>
		<edge from="n37" to="n37">
			<attr name="label">
				<string>newTemperature</string>
			</attr>
		</edge>
		<edge from="n34" to="n37">
			<attr name="label">
				<string>nameExpression</string>
			</attr>
		</edge>
		<node id="n38"/>
		<edge from="n38" to="n38">
			<attr name="label">
				<string>StringExpression</string>
			</attr>
		</edge>
		<edge from="n37" to="n38">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n39"/>
		<edge from="n39" to="n39">
			<attr name="label">
				<string>++</string>
			</attr>
		</edge>
		<edge from="n37" to="n39">
			<attr name="label">
				<string>symbol</string>
			</attr>
		</edge>
		<node id="n40"/>
		<edge from="n40" to="n40">
			<attr name="label">
				<string>Expression</string>
			</attr>
		</edge>
		<edge from="n37" to="n40">
			<attr name="label">
				<string>type</string>
			</attr>
		</edge>
		<edge from="n40" to="n2">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n41"/>
		<edge from="n41" to="n41">
			<attr name="label">
				<string>oldTemperature</string>
			</attr>
		</edge>
		<edge from="n34" to="n41">
			<attr name="label">
				<string>object</string>
			</attr>
		</edge>
		<node id="n42"/>
		<edge from="n42" to="n42">
			<attr name="label">
				<string>ActionInputPin</string>
			</attr>
		</edge>
		<edge from="n41" to="n42">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n43"/>
		<edge from="n43" to="n43">
			<attr name="label">
				<string>readCurrentTemperature</string>
			</attr>
		</edge>
		<edge from="n41" to="n43">
			<attr name="label">
				<string>fromAction</string>
			</attr>
		</edge>
		<node id="n44"/>
		<edge from="n44" to="n44">
			<attr name="label">
				<string>ReadStructuralFeatureAction</string>
			</attr>
		</edge>
		<edge from="n43" to="n44">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n43" to="n4">
			<attr name="label">
				<string>structuralFeature</string>
			</attr>
		</edge>
		<node id="n45"/>
		<edge from="n45" to="n45">
			<attr name="label">
				<string>foodPin</string>
			</attr>
		</edge>
		<edge from="n43" to="n45">
			<attr name="label">
				<string>object</string>
			</attr>
		</edge>
		<edge from="n45" to="n42">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n46"/>
		<edge from="n46" to="n46">
			<attr name="label">
				<string>getFood</string>
			</attr>
		</edge>
		<edge from="n45" to="n46">
			<attr name="label">
				<string>fromAction</string>
			</attr>
		</edge>
		<edge from="n46" to="n44">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n47"/>
		<edge from="n47" to="n47">
			<attr name="label">
				<string>cooks</string>
			</attr>
		</edge>
		<edge from="n46" to="n47">
			<attr name="label">
				<string>structuralFeature</string>
			</attr>
		</edge>
		<node id="n48"/>
		<edge from="n48" to="n48">
			<attr name="label">
				<string>selfActionInputPin</string>
			</attr>
		</edge>
		<edge from="n46" to="n48">
			<attr name="label">
				<string>object</string>
			</attr>
		</edge>
		<edge from="n48" to="n42">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n49"/>
		<edge from="n49" to="n49">
			<attr name="label">
				<string>selfRead</string>
			</attr>
		</edge>
		<edge from="n48" to="n49">
			<attr name="label">
				<string>fromAction</string>
			</attr>
		</edge>
		<node id="n50"/>
		<edge from="n50" to="n50">
			<attr name="label">
				<string>ReadSelfAction</string>
			</attr>
		</edge>
		<edge from="n49" to="n50">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n51"/>
		<edge from="n51" to="n51">
			<attr name="label">
				<string>selfPin</string>
			</attr>
		</edge>
		<edge from="n49" to="n51">
			<attr name="label">
				<string>result</string>
			</attr>
		</edge>
		<node id="n52"/>
		<edge from="n52" to="n52">
			<attr name="label">
				<string>OutputPin</string>
			</attr>
		</edge>
		<edge from="n51" to="n52">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n51" to="n36">
			<attr name="label">
				<string>isLeaf</string>
			</attr>
		</edge>
		<node id="n53"/>
		<edge from="n53" to="n53">
			<attr name="label">
				<string>theFood</string>
			</attr>
		</edge>
		<edge from="n46" to="n53">
			<attr name="label">
				<string>result</string>
			</attr>
		</edge>
		<edge from="n53" to="n52">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n53" to="n36">
			<attr name="label">
				<string>isLeaf</string>
			</attr>
		</edge>
		<node id="n54"/>
		<edge from="n54" to="n54">
			<attr name="label">
				<string>currentTemperature</string>
			</attr>
		</edge>
		<edge from="n43" to="n54">
			<attr name="label">
				<string>result</string>
			</attr>
		</edge>
		<edge from="n54" to="n52">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n54" to="n36">
			<attr name="label">
				<string>isLeaf</string>
			</attr>
		</edge>
		<node id="n55"/>
		<edge from="n55" to="n55">
			<attr name="label">
				<string>FinishedCooking</string>
			</attr>
		</edge>
		<edge from="n27" to="n55">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n55" to="n16">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n56"/>
		<edge from="n56" to="n56">
			<attr name="label">
				<string>FinishedCookingEntry</string>
			</attr>
		</edge>
		<edge from="n55" to="n56">
			<attr name="label">
				<string>entry</string>
			</attr>
		</edge>
		<edge from="n56" to="n33">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n57"/>
		<edge from="n57" to="n57">
			<attr name="label">
				<string>sendCooked</string>
			</attr>
		</edge>
		<edge from="n56" to="n57">
			<attr name="label">
				<string>node</string>
			</attr>
		</edge>
		<node id="n58"/>
		<edge from="n58" to="n58">
			<attr name="label">
				<string>SendSignalAction</string>
			</attr>
		</edge>
		<edge from="n57" to="n58">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n59"/>
		<edge from="n59" to="n59">
			<attr name="label">
				<string>cooked</string>
			</attr>
		</edge>
		<edge from="n57" to="n59">
			<attr name="label">
				<string>signal</string>
			</attr>
		</edge>
		<node id="n60"/>
		<edge from="n60" to="n60">
			<attr name="label">
				<string>foodPinB</string>
			</attr>
		</edge>
		<edge from="n57" to="n60">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n60" to="n42">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n61"/>
		<edge from="n61" to="n61">
			<attr name="label">
				<string>getFoodB</string>
			</attr>
		</edge>
		<edge from="n60" to="n61">
			<attr name="label">
				<string>fromAction</string>
			</attr>
		</edge>
		<edge from="n61" to="n44">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n61" to="n47">
			<attr name="label">
				<string>structuralFeature</string>
			</attr>
		</edge>
		<node id="n62"/>
		<edge from="n62" to="n62">
			<attr name="label">
				<string>selfActionInputPinB</string>
			</attr>
		</edge>
		<edge from="n61" to="n62">
			<attr name="label">
				<string>object</string>
			</attr>
		</edge>
		<edge from="n62" to="n42">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n63"/>
		<edge from="n63" to="n63">
			<attr name="label">
				<string>selfReadB</string>
			</attr>
		</edge>
		<edge from="n62" to="n63">
			<attr name="label">
				<string>fromAction</string>
			</attr>
		</edge>
		<edge from="n63" to="n50">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n64"/>
		<edge from="n64" to="n64">
			<attr name="label">
				<string>selfPinB</string>
			</attr>
		</edge>
		<edge from="n63" to="n64">
			<attr name="label">
				<string>result</string>
			</attr>
		</edge>
		<edge from="n64" to="n52">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n65"/>
		<edge from="n65" to="n65">
			<attr name="label">
				<string>theFoodB</string>
			</attr>
		</edge>
		<edge from="n61" to="n65">
			<attr name="label">
				<string>result</string>
			</attr>
		</edge>
		<edge from="n65" to="n52">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n66"/>
		<edge from="n66" to="n66">
			<attr name="label">
				<string>microwaveInitialTrans</string>
			</attr>
		</edge>
		<edge from="n27" to="n66">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n66" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n66" to="n30">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n66" to="n28">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n67"/>
		<edge from="n67" to="n67">
			<attr name="label">
				<string>microwaveFinalTrans</string>
			</attr>
		</edge>
		<edge from="n27" to="n67">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n67" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n67" to="n29">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n67" to="n55">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n68"/>
		<edge from="n68" to="n68">
			<attr name="label">
				<string>cookTrans</string>
			</attr>
		</edge>
		<edge from="n27" to="n68">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n68" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n68" to="n31">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n68" to="n30">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n69"/>
		<edge from="n69" to="n69">
			<attr name="label">
				<string>cookTrigger</string>
			</attr>
		</edge>
		<edge from="n68" to="n69">
			<attr name="label">
				<string>trigger</string>
			</attr>
		</edge>
		<edge from="n69" to="n23">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n70"/>
		<edge from="n70" to="n70">
			<attr name="label">
				<string>receiveCook</string>
			</attr>
		</edge>
		<edge from="n69" to="n70">
			<attr name="label">
				<string>event</string>
			</attr>
		</edge>
		<node id="n71"/>
		<edge from="n71" to="n71">
			<attr name="label">
				<string>stopCookingTrans</string>
			</attr>
		</edge>
		<edge from="n27" to="n71">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n71" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n71" to="n55">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n71" to="n31">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n72"/>
		<edge from="n72" to="n72">
			<attr name="label">
				<string>stopCookingTrigger</string>
			</attr>
		</edge>
		<edge from="n71" to="n72">
			<attr name="label">
				<string>trigger</string>
			</attr>
		</edge>
		<edge from="n72" to="n23">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n73"/>
		<edge from="n73" to="n73">
			<attr name="label">
				<string>receiveStopCooking</string>
			</attr>
		</edge>
		<edge from="n72" to="n73">
			<attr name="label">
				<string>event</string>
			</attr>
		</edge>
		<node id="n74"/>
		<edge from="n74" to="n74">
			<attr name="label">
				<string>Beeper</string>
			</attr>
		</edge>
		<edge from="n74" to="n2">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n75"/>
		<edge from="n75" to="n75">
			<attr name="label">
				<string>beeperSM</string>
			</attr>
		</edge>
		<edge from="n74" to="n75">
			<attr name="label">
				<string>classifierBehavior</string>
			</attr>
		</edge>
		<edge from="n74" to="n75">
			<attr name="label">
				<string>ownedBehavior</string>
			</attr>
		</edge>
		<edge from="n75" to="n8">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n76"/>
		<edge from="n76" to="n76">
			<attr name="label">
				<string>beeperSMregion</string>
			</attr>
		</edge>
		<edge from="n75" to="n76">
			<attr name="label">
				<string>region</string>
			</attr>
		</edge>
		<edge from="n76" to="n10">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n77"/>
		<edge from="n77" to="n77">
			<attr name="label">
				<string>beeperInitial</string>
			</attr>
		</edge>
		<edge from="n76" to="n77">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n77" to="n12">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n78"/>
		<edge from="n78" to="n78">
			<attr name="label">
				<string>beeperFinal</string>
			</attr>
		</edge>
		<edge from="n76" to="n78">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n78" to="n14">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n79"/>
		<edge from="n79" to="n79">
			<attr name="label">
				<string>Silent</string>
			</attr>
		</edge>
		<edge from="n76" to="n79">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n79" to="n16">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n80"/>
		<edge from="n80" to="n80">
			<attr name="label">
				<string>Beeping</string>
			</attr>
		</edge>
		<edge from="n76" to="n80">
			<attr name="label">
				<string>subvertex</string>
			</attr>
		</edge>
		<edge from="n80" to="n16">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n81"/>
		<edge from="n81" to="n81">
			<attr name="label">
				<string>beeperInitialTrans</string>
			</attr>
		</edge>
		<edge from="n76" to="n81">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n81" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n81" to="n79">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n81" to="n77">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n82"/>
		<edge from="n82" to="n82">
			<attr name="label">
				<string>beeperFinalTrans</string>
			</attr>
		</edge>
		<edge from="n76" to="n82">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n82" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n82" to="n78">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n82" to="n79">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n83"/>
		<edge from="n83" to="n83">
			<attr name="label">
				<string>beepTrans</string>
			</attr>
		</edge>
		<edge from="n76" to="n83">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n83" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n83" to="n80">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n83" to="n79">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n84"/>
		<edge from="n84" to="n84">
			<attr name="label">
				<string>beepTrigger</string>
			</attr>
		</edge>
		<edge from="n83" to="n84">
			<attr name="label">
				<string>trigger</string>
			</attr>
		</edge>
		<edge from="n84" to="n23">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n85"/>
		<edge from="n85" to="n85">
			<attr name="label">
				<string>receiveBeep</string>
			</attr>
		</edge>
		<edge from="n84" to="n85">
			<attr name="label">
				<string>event</string>
			</attr>
		</edge>
		<node id="n86"/>
		<edge from="n86" to="n86">
			<attr name="label">
				<string>stopBeepingTrans</string>
			</attr>
		</edge>
		<edge from="n76" to="n86">
			<attr name="label">
				<string>transition</string>
			</attr>
		</edge>
		<edge from="n86" to="n19">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n86" to="n79">
			<attr name="label">
				<string>target</string>
			</attr>
		</edge>
		<edge from="n86" to="n80">
			<attr name="label">
				<string>source</string>
			</attr>
		</edge>
		<node id="n87"/>
		<edge from="n87" to="n87">
			<attr name="label">
				<string>stopBeepingTrigger</string>
			</attr>
		</edge>
		<edge from="n86" to="n87">
			<attr name="label">
				<string>trigger</string>
			</attr>
		</edge>
		<edge from="n87" to="n23">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n88"/>
		<edge from="n88" to="n88">
			<attr name="label">
				<string>receiveStopBeeping</string>
			</attr>
		</edge>
		<edge from="n87" to="n88">
			<attr name="label">
				<string>event</string>
			</attr>
		</edge>
		<node id="n89"/>
		<edge from="n89" to="n89">
			<attr name="label">
				<string>ReceiveSignalEvent</string>
			</attr>
		</edge>
		<edge from="n24" to="n89">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n24" to="n59">
			<attr name="label">
				<string>signal</string>
			</attr>
		</edge>
		<node id="n90"/>
		<edge from="n90" to="n90">
			<attr name="label">
				<string>Signal</string>
			</attr>
		</edge>
		<edge from="n59" to="n90">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n73" to="n89">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n91"/>
		<edge from="n91" to="n91">
			<attr name="label">
				<string>stopCooking</string>
			</attr>
		</edge>
		<edge from="n73" to="n91">
			<attr name="label">
				<string>signal</string>
			</attr>
		</edge>
		<edge from="n91" to="n90">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n70" to="n89">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n92"/>
		<edge from="n92" to="n92">
			<attr name="label">
				<string>cook</string>
			</attr>
		</edge>
		<edge from="n70" to="n92">
			<attr name="label">
				<string>signal</string>
			</attr>
		</edge>
		<edge from="n92" to="n90">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n88" to="n89">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n93"/>
		<edge from="n93" to="n93">
			<attr name="label">
				<string>stopBeeping</string>
			</attr>
		</edge>
		<edge from="n88" to="n93">
			<attr name="label">
				<string>signal</string>
			</attr>
		</edge>
		<edge from="n93" to="n90">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n85" to="n89">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n94"/>
		<edge from="n94" to="n94">
			<attr name="label">
				<string>beep</string>
			</attr>
		</edge>
		<edge from="n85" to="n94">
			<attr name="label">
				<string>signal</string>
			</attr>
		</edge>
		<edge from="n94" to="n90">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n95"/>
		<edge from="n95" to="n95">
			<attr name="label">
				<string>Association_DP3</string>
			</attr>
		</edge>
		<node id="n96"/>
		<edge from="n96" to="n96">
			<attr name="label">
				<string>Association</string>
			</attr>
		</edge>
		<edge from="n95" to="n96">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n95" to="n47">
			<attr name="label">
				<string>memberEnd</string>
			</attr>
		</edge>
		<node id="n97"/>
		<edge from="n97" to="n97">
			<attr name="label">
				<string>cooked by</string>
			</attr>
		</edge>
		<edge from="n95" to="n97">
			<attr name="label">
				<string>memberEnd</string>
			</attr>
		</edge>
		<edge from="n95" to="n47">
			<attr name="label">
				<string>ownedEnd</string>
			</attr>
		</edge>
		<edge from="n47" to="n5">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n47" to="n1">
			<attr name="label">
				<string>type</string>
			</attr>
		</edge>
		<edge from="n47" to="n95">
			<attr name="label">
				<string>association</string>
			</attr>
		</edge>
		<node id="n98"/>
		<edge from="n98" to="n98">
			<attr name="label">
				<string>1</string>
			</attr>
		</edge>
		<edge from="n47" to="n98">
			<attr name="label">
				<string>upperValue</string>
			</attr>
		</edge>
		<node id="n99"/>
		<edge from="n99" to="n99">
			<attr name="label">
				<string>LiteralUnlimitedNatural</string>
			</attr>
		</edge>
		<edge from="n98" to="n99">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n47" to="n98">
			<attr name="label">
				<string>lowerValue</string>
			</attr>
		</edge>
		<node id="n100"/>
		<edge from="n100" to="n100">
			<attr name="label">
				<string>LiteralInteger</string>
			</attr>
		</edge>
		<edge from="n98" to="n100">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n95" to="n97">
			<attr name="label">
				<string>ownedEnd</string>
			</attr>
		</edge>
		<edge from="n97" to="n5">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n97" to="n25">
			<attr name="label">
				<string>type</string>
			</attr>
		</edge>
		<edge from="n97" to="n95">
			<attr name="label">
				<string>association</string>
			</attr>
		</edge>
		<edge from="n97" to="n98">
			<attr name="label">
				<string>upperValue</string>
			</attr>
		</edge>
		<edge from="n98" to="n99">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n97" to="n98">
			<attr name="label">
				<string>lowerValue</string>
			</attr>
		</edge>
		<edge from="n98" to="n100">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n101"/>
		<edge from="n101" to="n101">
			<attr name="label">
				<string>Association_EE7</string>
			</attr>
		</edge>
		<edge from="n101" to="n96">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n102"/>
		<edge from="n102" to="n102">
			<attr name="label">
				<string>has</string>
			</attr>
		</edge>
		<edge from="n101" to="n102">
			<attr name="label">
				<string>memberEnd</string>
			</attr>
		</edge>
		<node id="n103"/>
		<edge from="n103" to="n103">
			<attr name="label">
				<string>in</string>
			</attr>
		</edge>
		<edge from="n101" to="n103">
			<attr name="label">
				<string>memberEnd</string>
			</attr>
		</edge>
		<edge from="n101" to="n102">
			<attr name="label">
				<string>ownedEnd</string>
			</attr>
		</edge>
		<edge from="n102" to="n5">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n102" to="n74">
			<attr name="label">
				<string>type</string>
			</attr>
		</edge>
		<edge from="n102" to="n101">
			<attr name="label">
				<string>association</string>
			</attr>
		</edge>
		<edge from="n102" to="n98">
			<attr name="label">
				<string>upperValue</string>
			</attr>
		</edge>
		<edge from="n98" to="n99">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n102" to="n98">
			<attr name="label">
				<string>lowerValue</string>
			</attr>
		</edge>
		<edge from="n98" to="n100">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n101" to="n103">
			<attr name="label">
				<string>ownedEnd</string>
			</attr>
		</edge>
		<edge from="n103" to="n5">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n103" to="n25">
			<attr name="label">
				<string>type</string>
			</attr>
		</edge>
		<edge from="n103" to="n101">
			<attr name="label">
				<string>association</string>
			</attr>
		</edge>
		<edge from="n103" to="n98">
			<attr name="label">
				<string>upperValue</string>
			</attr>
		</edge>
		<edge from="n98" to="n99">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n103" to="n98">
			<attr name="label">
				<string>lowerValue</string>
			</attr>
		</edge>
		<edge from="n98" to="n100">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n104"/>
		<edge from="n104" to="n104">
			<attr name="label">
				<string>m1</string>
			</attr>
		</edge>
		<edge from="n104" to="n25">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n105"/>
		<edge from="n105" to="n105">
			<attr name="label">
				<string>m1classifierBehaviorExecution</string>
			</attr>
		</edge>
		<node id="n106"/>
		<edge from="n106" to="n106">
			<attr name="label">
				<string>BehaviorExecution</string>
			</attr>
		</edge>
		<edge from="n105" to="n106">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n104" to="n105">
			<attr name="label">
				<string>execution</string>
			</attr>
		</edge>
		<edge from="n105" to="n104">
			<attr name="label">
				<string>host</string>
			</attr>
		</edge>
		<edge from="n105" to="n26">
			<attr name="label">
				<string>behavior</string>
			</attr>
		</edge>
		<edge from="n105" to="n30">
			<attr name="label">
				<string>activeState</string>
			</attr>
		</edge>
		<node id="n107"/>
		<edge from="n107" to="n107">
			<attr name="label">
				<string>f1</string>
			</attr>
		</edge>
		<edge from="n107" to="n1">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n108"/>
		<edge from="n108" to="n108">
			<attr name="label">
				<string>f1classifierBehaviorExecution</string>
			</attr>
		</edge>
		<edge from="n108" to="n106">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n107" to="n108">
			<attr name="label">
				<string>execution</string>
			</attr>
		</edge>
		<edge from="n108" to="n107">
			<attr name="label">
				<string>host</string>
			</attr>
		</edge>
		<edge from="n108" to="n3">
			<attr name="label">
				<string>behavior</string>
			</attr>
		</edge>
		<edge from="n108" to="n15">
			<attr name="label">
				<string>activeState</string>
			</attr>
		</edge>
		<node id="n109"/>
		<edge from="n109" to="n109">
			<attr name="label">
				<string>3</string>
			</attr>
		</edge>
		<edge from="n107" to="n109">
			<attr name="label">
				<string>temperature</string>
			</attr>
		</edge>
		<node id="n110"/>
		<edge from="n110" to="n110">
			<attr name="label">
				<string>b1</string>
			</attr>
		</edge>
		<edge from="n110" to="n74">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n111"/>
		<edge from="n111" to="n111">
			<attr name="label">
				<string>b1classifierBehaviorExecution</string>
			</attr>
		</edge>
		<edge from="n111" to="n106">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n110" to="n111">
			<attr name="label">
				<string>execution</string>
			</attr>
		</edge>
		<edge from="n111" to="n110">
			<attr name="label">
				<string>host</string>
			</attr>
		</edge>
		<edge from="n111" to="n75">
			<attr name="label">
				<string>behavior</string>
			</attr>
		</edge>
		<edge from="n111" to="n79">
			<attr name="label">
				<string>activeState</string>
			</attr>
		</edge>
		<edge from="n104" to="n107">
			<attr name="label">
				<string>cooks</string>
			</attr>
		</edge>
		<edge from="n107" to="n104">
			<attr name="label">
				<string>cookedBy</string>
			</attr>
		</edge>
		<edge from="n104" to="n110">
			<attr name="label">
				<string>has</string>
			</attr>
		</edge>
		<edge from="n110" to="n104">
			<attr name="label">
				<string>in</string>
			</attr>
		</edge>
		<node id="n112"/>
		<edge from="n112" to="n112">
			<attr name="label">
				<string>a1</string>
			</attr>
		</edge>
		<node id="n113"/>
		<edge from="n113" to="n113">
			<attr name="label">
				<string>Actor</string>
			</attr>
		</edge>
		<edge from="n112" to="n113">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<node id="n114"/>
		<edge from="n114" to="n114">
			<attr name="label">
				<string>a1behaviorExecution</string>
			</attr>
		</edge>
		<edge from="n114" to="n106">
			<attr name="label">
				<string>i</string>
			</attr>
		</edge>
		<edge from="n112" to="n114">
			<attr name="label">
				<string>execution</string>
			</attr>
		</edge>
		<edge from="n114" to="n112">
			<attr name="label">
				<string>host</string>
			</attr>
		</edge>
	</graph>
</gxl>
