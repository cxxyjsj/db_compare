package com;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import com.bean.ArgsHolder;

@SpringBootApplication
public class Application extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {

    @Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
    	Map<String, Object> config = ArgsHolder.configs;
    	if(config.get("port") != null){
    		container.setPort(Integer.valueOf(config.get("port").toString()));
    	}else{
    		container.setPort(80);
    	}
	}

	public static void main(String[] args) {
		if(args != null && args.length > 0){
			for(int i=0;i<args.length;i++){
				if(args[i].startsWith("--") && i < args.length - 1){
					String key = args[i].replace("--", "");
					ArgsHolder.configs.put(key, args[i+1]);
				}
			}
		}
        SpringApplication.run(Application.class, args);
    }
}
