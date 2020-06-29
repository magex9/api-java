package ca.magex.json.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LoremIpsumGenerator {
	
	public static final List<String> PARAGRPAHS = List.of(
		"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Arcu non odio euismod lacinia at quis risus sed vulputate. Bibendum ut tristique et egestas quis ipsum suspendisse. Semper feugiat nibh sed pulvinar proin gravida hendrerit lectus a. Facilisis leo vel fringilla est. Turpis cursus in hac habitasse platea dictumst quisque sagittis purus. Egestas fringilla phasellus faucibus scelerisque eleifend donec pretium. Odio tempor orci dapibus ultrices in iaculis nunc sed augue. Semper feugiat nibh sed pulvinar proin gravida hendrerit lectus a. Ipsum dolor sit amet consectetur. Luctus accumsan tortor posuere ac ut consequat semper viverra. Auctor neque vitae tempus quam pellentesque nec. Nunc sed blandit libero volutpat sed cras. Justo eget magna fermentum iaculis eu non. Semper eget duis at tellus at urna condimentum. Rutrum quisque non tellus orci ac. Dictumst quisque sagittis purus sit amet volutpat.",
		"Tincidunt praesent semper feugiat nibh. Sit amet tellus cras adipiscing enim eu. Sagittis id consectetur purus ut. Et magnis dis parturient montes. Vel pharetra vel turpis nunc eget. Quis eleifend quam adipiscing vitae proin sagittis nisl. Hac habitasse platea dictumst vestibulum rhoncus. Lacus vestibulum sed arcu non odio euismod lacinia at. Consectetur adipiscing elit ut aliquam. Dignissim cras tincidunt lobortis feugiat vivamus at augue eget. Ipsum suspendisse ultrices gravida dictum fusce ut placerat orci. Tortor posuere ac ut consequat semper viverra nam. Massa tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada. Elementum pulvinar etiam non quam lacus suspendisse faucibus interdum posuere. Aliquam vestibulum morbi blandit cursus risus at. Nisl nunc mi ipsum faucibus vitae aliquet. Viverra orci sagittis eu volutpat odio facilisis mauris sit. Mauris ultrices eros in cursus turpis massa. Morbi tristique senectus et netus et malesuada fames ac turpis. Amet luctus venenatis lectus magna.",
		"Vestibulum lectus mauris ultrices eros in cursus turpis massa. Hac habitasse platea dictumst vestibulum rhoncus est pellentesque. Donec ac odio tempor orci dapibus. Mattis aliquam faucibus purus in massa. Odio facilisis mauris sit amet massa vitae tortor condimentum. Placerat in egestas erat imperdiet sed euismod. Lobortis mattis aliquam faucibus purus in massa tempor. Eleifend mi in nulla posuere sollicitudin aliquam. Vestibulum morbi blandit cursus risus. Rhoncus urna neque viverra justo.",
		"Cursus eget nunc scelerisque viverra mauris. Fames ac turpis egestas maecenas. In vitae turpis massa sed. Fermentum et sollicitudin ac orci phasellus. Luctus accumsan tortor posuere ac ut consequat semper viverra nam. In aliquam sem fringilla ut morbi tincidunt. Platea dictumst quisque sagittis purus sit amet volutpat consequat. Massa massa ultricies mi quis hendrerit dolor magna eget est. Habitasse platea dictumst quisque sagittis purus sit. Ac felis donec et odio pellentesque diam. Convallis convallis tellus id interdum velit laoreet id donec ultrices. Enim nec dui nunc mattis enim ut. Volutpat blandit aliquam etiam erat velit scelerisque in dictum. Id aliquet lectus proin nibh nisl condimentum id. At varius vel pharetra vel turpis nunc eget. Lacus luctus accumsan tortor posuere ac ut consequat semper viverra. Lacus luctus accumsan tortor posuere ac ut consequat. Eu sem integer vitae justo eget magna fermentum iaculis.",
		"Volutpat est velit egestas dui id ornare. Lorem sed risus ultricies tristique nulla aliquet enim. Dictum fusce ut placerat orci nulla pellentesque. Condimentum mattis pellentesque id nibh tortor id aliquet. Pharetra et ultrices neque ornare aenean euismod elementum nisi quis. Mauris cursus mattis molestie a iaculis at erat pellentesque adipiscing. Blandit cursus risus at ultrices mi tempus. Risus in hendrerit gravida rutrum quisque non. Gravida neque convallis a cras. Id diam vel quam elementum pulvinar etiam non quam. Aliquam vestibulum morbi blandit cursus. Amet mauris commodo quis imperdiet massa tincidunt nunc pulvinar.",
		"Nisi porta lorem mollis aliquam. Vulputate eu scelerisque felis imperdiet proin fermentum leo. Enim nunc faucibus a pellentesque sit amet. Nisl suscipit adipiscing bibendum est ultricies integer quis auctor. A erat nam at lectus urna. Dolor sit amet consectetur adipiscing elit. Tellus in metus vulputate eu. Convallis a cras semper auctor neque. Urna molestie at elementum eu facilisis sed odio. Sed id semper risus in hendrerit gravida. Sed sed risus pretium quam vulputate. Dictumst quisque sagittis purus sit amet volutpat. Morbi tristique senectus et netus et.", 
		"Tellus molestie nunc non blandit massa enim nec. Penatibus et magnis dis parturient montes nascetur. Sed faucibus turpis in eu mi bibendum neque. Neque sodales ut etiam sit amet nisl purus in. Fringilla est ullamcorper eget nulla facilisi. Amet cursus sit amet dictum sit. Hendrerit gravida rutrum quisque non tellus. Convallis posuere morbi leo urna molestie at elementum eu. Et magnis dis parturient montes nascetur. Lectus arcu bibendum at varius vel pharetra.", 
		"Pharetra massa massa ultricies mi quis hendrerit. Cras sed felis eget velit aliquet sagittis. Tortor at auctor urna nunc. Morbi non arcu risus quis varius quam quisque. Placerat duis ultricies lacus sed turpis tincidunt. Eget est lorem ipsum dolor sit amet consectetur. Donec massa sapien faucibus et. Turpis nunc eget lorem dolor sed. Commodo viverra maecenas accumsan lacus vel facilisis volutpat est. Aliquam sem fringilla ut morbi tincidunt augue. Placerat in egestas erat imperdiet sed euismod nisi.", 
		"Sed elementum tempus egestas sed sed risus pretium quam. Nec feugiat nisl pretium fusce id. Eget arcu dictum varius duis. Odio euismod lacinia at quis. Tristique nulla aliquet enim tortor at auctor urna. Varius vel pharetra vel turpis nunc eget lorem. Sem integer vitae justo eget magna. In arcu cursus euismod quis. Massa vitae tortor condimentum lacinia quis vel eros donec. Gravida dictum fusce ut placerat orci nulla. Diam maecenas sed enim ut sem. Tempus quam pellentesque nec nam.", 
		"Sodales ut etiam sit amet nisl purus in. Sit amet consectetur adipiscing elit pellentesque. Id volutpat lacus laoreet non curabitur gravida. Mattis aliquam faucibus purus in massa. Amet est placerat in egestas erat. Libero enim sed faucibus turpis in eu. Egestas sed tempus urna et pharetra pharetra. Vel risus commodo viverra maecenas. Vivamus arcu felis bibendum ut tristique et egestas quis ipsum. Tellus in metus vulputate eu scelerisque felis imperdiet. Tincidunt praesent semper feugiat nibh sed pulvinar proin gravida. Eget sit amet tellus cras adipiscing enim eu.", 
		"Arcu ac tortor dignissim convallis aenean et tortor at risus. Scelerisque eu ultrices vitae auctor eu. Eleifend donec pretium vulputate sapien nec. Enim lobortis scelerisque fermentum dui faucibus in ornare quam viverra. Risus ultricies tristique nulla aliquet enim tortor at auctor urna. Eget mi proin sed libero enim sed faucibus turpis in. Fringilla urna porttitor rhoncus dolor purus non. Leo vel fringilla est ullamcorper eget nulla facilisi etiam. Eleifend quam adipiscing vitae proin. Aliquam etiam erat velit scelerisque in dictum non consectetur. In vitae turpis massa sed elementum tempus egestas sed sed. Adipiscing enim eu turpis egestas pretium aenean pharetra magna. Sit amet porttitor eget dolor morbi non. At urna condimentum mattis pellentesque. Faucibus scelerisque eleifend donec pretium vulputate. Nam libero justo laoreet sit amet cursus. Pellentesque id nibh tortor id aliquet lectus proin nibh nisl. Mollis nunc sed id semper risus in hendrerit. Mattis aliquam faucibus purus in massa tempor.", 
		"Eget mauris pharetra et ultrices neque ornare aenean euismod. Ac ut consequat semper viverra nam libero. Libero nunc consequat interdum varius sit amet. Augue ut lectus arcu bibendum at. Volutpat sed cras ornare arcu dui vivamus arcu felis bibendum. Ut pharetra sit amet aliquam. Nunc sed blandit libero volutpat sed cras ornare. Amet cursus sit amet dictum sit. Odio pellentesque diam volutpat commodo sed. Nunc lobortis mattis aliquam faucibus. Vulputate sapien nec sagittis aliquam. Laoreet non curabitur gravida arcu ac tortor dignissim convallis. Velit ut tortor pretium viverra suspendisse potenti nullam. Pharetra vel turpis nunc eget lorem dolor sed viverra ipsum. Gravida cum sociis natoque penatibus. Est placerat in egestas erat imperdiet. Molestie ac feugiat sed lectus vestibulum mattis ullamcorper velit sed. Pharetra sit amet aliquam id diam maecenas ultricies. Cursus vitae congue mauris rhoncus aenean vel. Scelerisque purus semper eget duis at tellus at urna.", 
		"Morbi tempus iaculis urna id volutpat lacus. Tempor commodo ullamcorper a lacus vestibulum sed arcu. Malesuada nunc vel risus commodo viverra maecenas accumsan lacus vel. A cras semper auctor neque vitae tempus quam. A iaculis at erat pellentesque adipiscing commodo elit at imperdiet. Sed tempus urna et pharetra pharetra massa massa. Tellus in hac habitasse platea dictumst. Risus commodo viverra maecenas accumsan lacus. Sit amet mattis vulputate enim nulla aliquet porttitor. Ac auctor augue mauris augue neque gravida.", 
		"Eu facilisis sed odio morbi quis commodo odio. Facilisi morbi tempus iaculis urna id volutpat lacus. Integer malesuada nunc vel risus commodo viverra maecenas accumsan. Adipiscing elit duis tristique sollicitudin nibh sit amet commodo. Commodo elit at imperdiet dui accumsan. Accumsan sit amet nulla facilisi morbi tempus iaculis. Lorem ipsum dolor sit amet consectetur adipiscing elit pellentesque. Nec ullamcorper sit amet risus. Pellentesque sit amet porttitor eget dolor morbi non arcu risus. Enim nunc faucibus a pellentesque sit amet. Urna cursus eget nunc scelerisque viverra mauris. Velit ut tortor pretium viverra suspendisse potenti nullam ac.", 
		"Rutrum quisque non tellus orci. Et malesuada fames ac turpis egestas. Nullam ac tortor vitae purus. Nulla facilisi nullam vehicula ipsum. In pellentesque massa placerat duis ultricies lacus. Nullam ac tortor vitae purus faucibus ornare suspendisse sed nisi. Enim nunc faucibus a pellentesque sit amet porttitor eget dolor. Dignissim convallis aenean et tortor at risus viverra. Lectus urna duis convallis convallis tellus id. Etiam sit amet nisl purus in mollis nunc sed. Sit amet luctus venenatis lectus magna fringilla urna. Risus commodo viverra maecenas accumsan. Lacus viverra vitae congue eu. Justo nec ultrices dui sapien eget mi proin sed libero. Porta lorem mollis aliquam ut porttitor leo a diam sollicitudin. Convallis convallis tellus id interdum velit. Neque gravida in fermentum et sollicitudin.", 
		"Nulla facilisi etiam dignissim diam quis. Lacus suspendisse faucibus interdum posuere. A arcu cursus vitae congue mauris rhoncus aenean. Phasellus faucibus scelerisque eleifend donec pretium. Metus aliquam eleifend mi in. Egestas pretium aenean pharetra magna ac placerat vestibulum lectus mauris. Augue ut lectus arcu bibendum at varius. Aliquam ultrices sagittis orci a scelerisque purus semper eget. Nibh tellus molestie nunc non blandit massa enim nec. Id aliquet lectus proin nibh nisl condimentum id venenatis a. Dignissim suspendisse in est ante in nibh mauris cursus mattis. Et tortor at risus viverra adipiscing at in. Ornare suspendisse sed nisi lacus sed viverra. Et malesuada fames ac turpis. Turpis cursus in hac habitasse platea dictumst. In ornare quam viverra orci sagittis eu volutpat odio facilisis. Urna duis convallis convallis tellus id interdum. Dictum varius duis at consectetur lorem donec. Eget egestas purus viverra accumsan in nisl nisi.", 
		"Varius sit amet mattis vulputate enim nulla aliquet porttitor lacus. Arcu non odio euismod lacinia at quis risus sed. Id diam vel quam elementum pulvinar. Arcu dictum varius duis at. Purus in mollis nunc sed id semper risus in. Nisi est sit amet facilisis magna etiam. Et tortor at risus viverra adipiscing at in. Eu ultrices vitae auctor eu. Nec feugiat in fermentum posuere urna nec. Mattis vulputate enim nulla aliquet. Ornare arcu dui vivamus arcu felis bibendum ut tristique.", 
		"Vestibulum rhoncus est pellentesque elit ullamcorper dignissim. Fermentum iaculis eu non diam phasellus vestibulum lorem. Accumsan tortor posuere ac ut. Ullamcorper eget nulla facilisi etiam dignissim diam quis. Tristique sollicitudin nibh sit amet commodo. Nibh cras pulvinar mattis nunc. Nullam ac tortor vitae purus faucibus ornare suspendisse sed. Rutrum quisque non tellus orci ac. Aliquam sem fringilla ut morbi. Pharetra pharetra massa massa ultricies mi quis hendrerit dolor. Consectetur adipiscing elit ut aliquam. At imperdiet dui accumsan sit amet nulla facilisi. Risus quis varius quam quisque id diam vel quam. Feugiat vivamus at augue eget arcu dictum varius duis.", 
		"Eleifend mi in nulla posuere sollicitudin aliquam ultrices sagittis orci. Sem fringilla ut morbi tincidunt augue interdum. Integer malesuada nunc vel risus commodo viverra maecenas. Egestas maecenas pharetra convallis posuere morbi leo urna molestie at. Consectetur adipiscing elit ut aliquam purus. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Lacinia quis vel eros donec ac odio tempor. Aenean vel elit scelerisque mauris pellentesque pulvinar pellentesque. Fames ac turpis egestas sed tempus urna et pharetra pharetra. Neque ornare aenean euismod elementum nisi quis eleifend. Metus dictum at tempor commodo ullamcorper a lacus vestibulum.",
		"At imperdiet dui accumsan sit amet nulla facilisi. Tortor aliquam nulla facilisi cras fermentum odio eu. Condimentum vitae sapien pellentesque habitant morbi tristique senectus et. Pharetra sit amet aliquam id diam maecenas. Mattis rhoncus urna neque viverra justo nec ultrices dui. Venenatis cras sed felis eget velit aliquet sagittis id. Tortor posuere ac ut consequat semper viverra nam. Aliquet nec ullamcorper sit amet risus nullam eget felis. Quis imperdiet massa tincidunt nunc pulvinar. Sed sed risus pretium quam vulputate dignissim suspendisse in.",
		"Imperdiet massa tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada. Pellentesque id nibh tortor id. Egestas tellus rutrum tellus pellentesque eu. Morbi tristique senectus et netus et malesuada fames ac. Volutpat maecenas volutpat blandit aliquam etiam erat velit. Eleifend mi in nulla posuere sollicitudin aliquam ultrices sagittis orci. Elementum pulvinar etiam non quam lacus suspendisse. Arcu odio ut sem nulla pharetra diam sit amet. Nec tincidunt praesent semper feugiat nibh sed pulvinar. Malesuada nunc vel risus commodo viverra maecenas accumsan lacus vel. Sodales neque sodales ut etiam sit amet. Pharetra et ultrices neque ornare. Feugiat in fermentum posuere urna nec tincidunt praesent. In hac habitasse platea dictumst vestibulum rhoncus est.",
		"Lectus vestibulum mattis ullamcorper velit sed. Nulla aliquet enim tortor at auctor. Mauris pharetra et ultrices neque. Nec ultrices dui sapien eget. Nunc mattis enim ut tellus elementum sagittis vitae. Pretium viverra suspendisse potenti nullam ac. Interdum varius sit amet mattis. Nisl tincidunt eget nullam non. Vulputate dignissim suspendisse in est ante. Dolor magna eget est lorem ipsum dolor sit. Tortor at auctor urna nunc id cursus metus aliquam eleifend. Dignissim diam quis enim lobortis scelerisque fermentum dui faucibus. Ultricies tristique nulla aliquet enim tortor at auctor urna nunc. Praesent elementum facilisis leo vel. Dignissim suspendisse in est ante.",
		"Pharetra magna ac placerat vestibulum. Massa vitae tortor condimentum lacinia quis. Pharetra et ultrices neque ornare aenean euismod elementum nisi. Orci a scelerisque purus semper eget duis. Et tortor at risus viverra. Proin fermentum leo vel orci porta non pulvinar neque. Sagittis id consectetur purus ut faucibus pulvinar elementum integer enim. Id porta nibh venenatis cras sed. Facilisis gravida neque convallis a cras semper auctor. Eget magna fermentum iaculis eu non diam phasellus vestibulum. Eu nisl nunc mi ipsum faucibus. Urna nunc id cursus metus aliquam eleifend.",
		"Lectus mauris ultrices eros in cursus turpis. Nunc sed id semper risus in hendrerit gravida rutrum. A pellentesque sit amet porttitor eget dolor morbi non arcu. Natoque penatibus et magnis dis parturient. Non blandit massa enim nec dui nunc mattis enim ut. Ipsum dolor sit amet consectetur adipiscing elit duis. Tellus cras adipiscing enim eu turpis egestas pretium aenean pharetra. Ut faucibus pulvinar elementum integer enim neque volutpat ac tincidunt. Lacus luctus accumsan tortor posuere ac ut consequat. Pretium lectus quam id leo in vitae. Dictumst vestibulum rhoncus est pellentesque elit ullamcorper dignissim cras. Ullamcorper malesuada proin libero nunc.",
		"Feugiat nisl pretium fusce id velit ut tortor pretium viverra. Morbi quis commodo odio aenean sed adipiscing. Bibendum ut tristique et egestas quis ipsum. Diam volutpat commodo sed egestas egestas. At tellus at urna condimentum mattis pellentesque. Adipiscing commodo elit at imperdiet dui accumsan sit. Etiam sit amet nisl purus in mollis nunc. Id consectetur purus ut faucibus pulvinar. Sit amet mattis vulputate enim nulla aliquet porttitor lacus. Consectetur adipiscing elit pellentesque habitant. Fusce ut placerat orci nulla pellentesque dignissim enim sit.",
		"Convallis a cras semper auctor neque vitae tempus quam pellentesque. Gravida rutrum quisque non tellus orci ac. Dui ut ornare lectus sit amet est. Maecenas accumsan lacus vel facilisis volutpat est velit. Eleifend mi in nulla posuere sollicitudin. Ipsum faucibus vitae aliquet nec ullamcorper sit amet risus nullam. Quam elementum pulvinar etiam non quam lacus suspendisse faucibus. Ut faucibus pulvinar elementum integer. Sapien pellentesque habitant morbi tristique senectus et netus. Vulputate enim nulla aliquet porttitor lacus luctus accumsan tortor posuere. Nunc sed velit dignissim sodales. Integer feugiat scelerisque varius morbi enim nunc faucibus a. Commodo elit at imperdiet dui. Pretium viverra suspendisse potenti nullam ac tortor vitae purus faucibus.",
		"Facilisis leo vel fringilla est ullamcorper eget. Pellentesque dignissim enim sit amet venenatis urna. Enim ut tellus elementum sagittis. Ultricies integer quis auctor elit. Porttitor massa id neque aliquam. Ut tristique et egestas quis ipsum suspendisse. Nunc eget lorem dolor sed viverra ipsum nunc. Nisl pretium fusce id velit. Mauris a diam maecenas sed enim ut sem viverra. Nunc eget lorem dolor sed viverra. Non blandit massa enim nec dui nunc. Elit pellentesque habitant morbi tristique senectus et. Habitasse platea dictumst quisque sagittis purus sit amet volutpat. Eget gravida cum sociis natoque penatibus et. Vel pretium lectus quam id leo in. Accumsan lacus vel facilisis volutpat est. Pretium nibh ipsum consequat nisl vel pretium. Sociis natoque penatibus et magnis dis parturient montes. Egestas quis ipsum suspendisse ultrices gravida dictum fusce ut. Tristique senectus et netus et malesuada fames.",
		"Tortor at risus viverra adipiscing at. Augue interdum velit euismod in pellentesque massa placerat. Eget nunc lobortis mattis aliquam faucibus. Egestas pretium aenean pharetra magna ac placerat vestibulum. Orci dapibus ultrices in iaculis nunc sed augue lacus viverra. Molestie ac feugiat sed lectus. Lorem sed risus ultricies tristique nulla aliquet enim tortor at. Ultrices tincidunt arcu non sodales neque sodales ut etiam sit. Volutpat commodo sed egestas egestas. Faucibus in ornare quam viverra orci. Diam donec adipiscing tristique risus nec feugiat. Turpis massa sed elementum tempus egestas. Quis commodo odio aenean sed adipiscing diam donec adipiscing. Enim tortor at auctor urna nunc id. Pellentesque elit eget gravida cum sociis. Turpis nunc eget lorem dolor sed viverra.",
		"Pharetra et ultrices neque ornare. Ultricies tristique nulla aliquet enim tortor. Ultrices tincidunt arcu non sodales neque sodales. Bibendum enim facilisis gravida neque. Neque ornare aenean euismod elementum. Dui vivamus arcu felis bibendum ut tristique et egestas quis. Sagittis orci a scelerisque purus semper eget. Feugiat pretium nibh ipsum consequat nisl vel pretium. Arcu cursus vitae congue mauris rhoncus aenean vel elit. Euismod nisi porta lorem mollis aliquam ut. Leo a diam sollicitudin tempor id eu nisl nunc mi. Purus in mollis nunc sed. Elit duis tristique sollicitudin nibh sit amet commodo nulla. Non odio euismod lacinia at quis risus sed vulputate odio. Mus mauris vitae ultricies leo integer malesuada nunc vel risus. Magna ac placerat vestibulum lectus mauris ultrices eros in cursus. Sed vulputate mi sit amet mauris commodo. Ut sem nulla pharetra diam sit amet nisl suscipit. Sit amet consectetur adipiscing elit ut.",
		"Urna et pharetra pharetra massa massa ultricies mi. Tellus elementum sagittis vitae et leo. Dolor sed viverra ipsum nunc aliquet bibendum enim facilisis. Vel pharetra vel turpis nunc eget lorem dolor. Tortor dignissim convallis aenean et tortor at risus viverra adipiscing. Tempor orci eu lobortis elementum. Eget mauris pharetra et ultrices neque ornare aenean euismod elementum. Orci sagittis eu volutpat odio facilisis mauris sit amet. Lectus urna duis convallis convallis tellus id. Nisl rhoncus mattis rhoncus urna.",
		"Ultricies mi eget mauris pharetra et ultrices neque. Pretium aenean pharetra magna ac placerat vestibulum lectus. Semper quis lectus nulla at volutpat diam ut. Morbi tristique senectus et netus et malesuada fames ac turpis. Suspendisse interdum consectetur libero id faucibus nisl. Elit pellentesque habitant morbi tristique senectus et netus et. Dapibus ultrices in iaculis nunc sed augue lacus. Viverra accumsan in nisl nisi. Scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus. Malesuada nunc vel risus commodo viverra maecenas accumsan lacus. Sed id semper risus in hendrerit gravida rutrum quisque non. Mauris commodo quis imperdiet massa tincidunt nunc pulvinar sapien. Vitae turpis massa sed elementum tempus egestas sed sed risus. Massa tincidunt dui ut ornare lectus sit amet est placerat. Netus et malesuada fames ac turpis egestas sed tempus.",
		"Sit amet massa vitae tortor condimentum. Ornare aenean euismod elementum nisi quis eleifend quam adipiscing vitae. Sit amet est placerat in egestas erat imperdiet sed. Quis viverra nibh cras pulvinar mattis nunc sed. In ante metus dictum at tempor commodo. Lacinia quis vel eros donec. Ipsum dolor sit amet consectetur adipiscing elit pellentesque. Lobortis elementum nibh tellus molestie nunc non. Nibh nisl condimentum id venenatis a condimentum. Non consectetur a erat nam at. Vulputate mi sit amet mauris commodo quis imperdiet massa.",
		"Cursus risus at ultrices mi tempus imperdiet nulla. Accumsan tortor posuere ac ut consequat semper. Eu facilisis sed odio morbi quis. Vel orci porta non pulvinar neque laoreet suspendisse interdum consectetur. Bibendum neque egestas congue quisque egestas. Molestie at elementum eu facilisis sed odio morbi quis. Aenean sed adipiscing diam donec adipiscing tristique risus. Diam volutpat commodo sed egestas egestas. Velit euismod in pellentesque massa placerat duis. Sed adipiscing diam donec adipiscing tristique risus. Mi sit amet mauris commodo quis imperdiet massa tincidunt. Malesuada fames ac turpis egestas integer. Consectetur libero id faucibus nisl tincidunt eget nullam non nisi. Faucibus et molestie ac feugiat sed lectus vestibulum. Lorem sed risus ultricies tristique nulla aliquet enim tortor."
	);
	
	public static final String buildWords(int words) {
		if (words == 0)
			return "";
		List<String> WORDS = Arrays.asList(PARAGRPAHS.get(0).split("\\s"));
		StringBuilder sb = new StringBuilder();
		int w = 0;
		for (int i = 0; i < words; i++) {
			sb.append(" ");
			sb.append(WORDS.get(w++));
			if (w >= WORDS.size())
				w = 0;
		}
		return sb.substring(1);
	}
	
	public static final String randomWords(int words) {
		Random rand = new Random();
		if (words == 0)
			return "";
		List<String> WORDS = Arrays.asList(PARAGRPAHS.get(rand.nextInt(PARAGRPAHS.size())).split("\\s"));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words; i++) {
			sb.append(" ");
			sb.append(WORDS.get(rand.nextInt(WORDS.size() - 1)));
		}
		return sb.substring(1);
	}
	
	public static final String buildParagraphs(int paragraphs) {
		if (paragraphs == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		int w = 0;
		for (int i = 0; i < paragraphs; i++) {
			sb.append("\n");
			sb.append(PARAGRPAHS.get(w++));
			if (w >= PARAGRPAHS.size())
				w = 0;
		}
		return sb.substring(1);
	}
	
	public static final String randomParagraphs(int paragraphs) {
		Random rand = new Random();
		if (paragraphs == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < paragraphs; i++) {
			sb.append("\n");
			sb.append(PARAGRPAHS.get(rand.nextInt(PARAGRPAHS.size() - 1)));
		}
		return sb.substring(1);
	}

}
