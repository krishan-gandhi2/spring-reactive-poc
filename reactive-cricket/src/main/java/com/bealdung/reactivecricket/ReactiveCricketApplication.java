package com.bealdung.reactivecricket;



import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

@SpringBootApplication
public class ReactiveCricketApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveCricketApplication.class, args);
	}
	


    List<Player> playerList = new ArrayList<>();
    List<String> countryList = Arrays.asList("India,Australia,England,SriLanka".split(","));
    List<String> playerNames = Arrays.asList("Virat,Smith,Kallis,Sachin,Rohit,Steve,Dhoni".split(","));

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            createRandomStock();
        };
    }

    @RestController
    @RequestMapping("/player/records")
    class StockTransactionController {

        @Autowired
        StockTransactionService stockTransactionService;

        @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
        public Flux<BatsmanRecord> stockTransactionEvents(){
            return stockTransactionService.getStockTransactions();
        }
    }

    @Service
    class StockTransactionService {
        Flux<BatsmanRecord> getStockTransactions() {
            Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
            interval.subscribe((i) -> playerList.forEach(player -> player.setCountry(changeCountry())));

            Flux<BatsmanRecord> stockTransactionFlux = Flux.fromStream(Stream.generate(() -> new BatsmanRecord(getRandomRuns(), getRandomPlayer(), new Date())));
            return Flux.zip(interval, stockTransactionFlux).map(Tuple2::getT2);
        }
    }

    class Player {
        public Player(String name, String country) {
			super();
			this.name = name;
			this.country = country;
		}
		String name;
        String country;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		@Override
		public String toString() {
			return "name :-"+name+" country:-"+country;
		}
        
    }

    class BatsmanRecord {
        public BatsmanRecord(Integer runs, Player player, Date when) {
			super();
			this.runs = runs;
			this.player = player;
			this.when = when;
		}
		Integer runs;
		Player player;
        Date when;
        
		public Integer getRuns() {
			return runs;
		}

		public void setRuns(Integer runs) {
			this.runs = runs;
		}

		public Player getPlayer() {
			return player;
		}

		public void setPlayer(Player player) {
			this.player = player;
		}

		public Date getWhen() {
			return when;
		}

		public void setWhen(Date when) {
			this.when = when;
		}

		@Override
		public String toString() {
			return "Runs :-"+runs+" Player:-"+player+" when:-" +when;
		}
    }

    void createRandomStock() {
    	playerNames.forEach(player -> {
            playerList.add(new Player(player, generateRandomCountry()));
        });
    }

    String generateRandomCountry() {
    	return countryList.get(new Random().nextInt(countryList.size()));
    }

    String changeCountry() {
    	return countryList.get(new Random().nextInt(countryList.size()));
    }

    Integer getRandomRuns() {
    	Integer min = 30;
        Integer max = 50;
        return  new Random().nextInt() * (max - min);
    }

    Player getRandomPlayer() {
        return playerList.get(new Random().nextInt(playerList.size()));
    }

    float roundFloat(float number) {
        return Math.round(number * 100.0) / 100.0f;
    }

}
