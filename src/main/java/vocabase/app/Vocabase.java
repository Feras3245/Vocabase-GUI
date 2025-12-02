package vocabase.app;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = {"vocabase.app","vocabase.service", "vocabase.controller", "vocabase.utils"})
public class Vocabase {
	public static void main(String[] args) throws IOException {
		new SpringApplicationBuilder(Vocabase.class).properties("spring.application.name=Vocabase").build(args).run();
	}
}