/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qainfotech.tap.training.snl.api;

/**
 *
 * @author avijitkumar
 */
import static org.testng.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.qainfotech.tap.training.snl.api.Board;
import com.qainfotech.tap.training.snl.api.GameInProgressException;
import com.qainfotech.tap.training.snl.api.MaxPlayersReachedExeption;
import com.qainfotech.tap.training.snl.api.NoUserWithSuchUUIDException;
import com.qainfotech.tap.training.snl.api.PlayerExistsException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BoardTest {

	Board board;
	
	@BeforeTest
	public void createBoard() throws UnsupportedEncodingException, IOException, PlayerExistsException, GameInProgressException, FileNotFoundException, MaxPlayersReachedExeption {
		board= new Board();
		board.registerPlayer("Avijit");
		board.registerPlayer("Ayushi");
		board.registerPlayer("Navendu");
		
		
	}
	
	@Test(expectedExceptions=PlayerExistsException.class)
	public void aTestPlayerExistsException() throws Exception{
		board.registerPlayer("Avijit");
	}
	
	@Test(expectedExceptions=MaxPlayersReachedExeption.class)
	public void bTestMaxPlayersReachedException() throws Exception{
		
		board.registerPlayer("4thPlayer");
		board.registerPlayer("5thPlayer");	
	}
	
	@Test
	public void cTestInitialPosition() throws Exception{
		board= new Board();
		JSONArray player=new JSONArray();
		player=board.registerPlayer("ABC");
		Object pos=player.get(0);
		JSONObject jpos=(JSONObject) pos;
		assertEquals(jpos.getInt("position"),0);
		
	}
	
	@Test(expectedExceptions = GameInProgressException.class)
	public void GameInProgressExceptionTest() throws com.qainfotech.tap.training.snl.api.InvalidTurnException,
			com.qainfotech.tap.training.snl.api.PlayerExistsException,
			com.qainfotech.tap.training.snl.api.GameInProgressException,
			com.qainfotech.tap.training.snl.api.MaxPlayersReachedExeption, IOException, NoUserWithSuchUUIDException { 
		Board board= new Board();
		board.registerPlayer("Piyush0");
		board.registerPlayer("Piyush1");
		UUID uuid = UUID
				.fromString(((JSONObject) board.getData().getJSONArray("players").get(0)).get("uuid").toString());
		board.rollDice(uuid);
		board.registerPlayer("Piyush");
	}
 
	@Test(expectedExceptions = InvalidTurnException.class)
	public void InvalidTurnExceptionTest() throws com.qainfotech.tap.training.snl.api.InvalidTurnException, IOException, PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption {
		Board board= new Board();
		board.registerPlayer("Piyush");
		board.registerPlayer("Piyush1");
		board.registerPlayer("Piyush2");
		UUID uuid1 = UUID
				.fromString(((JSONObject) board.getData().getJSONArray("players").get(1)).get("uuid").toString());
		board.rollDice(uuid1);
	}

	@Test(expectedExceptions = NoUserWithSuchUUIDException.class)
	public void NoUerWithSuchUUIDExceptionTest() throws com.qainfotech.tap.training.snl.api.InvalidTurnException, NoUserWithSuchUUIDException, IOException, PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption {
		Board board= new Board();
		board.registerPlayer("Piyush");
		board.registerPlayer("Piyush1");
		UUID uuid = UUID.randomUUID();
		board.deletePlayer(uuid);

	}

	@Test
	public void test1() throws FileNotFoundException, UnsupportedEncodingException, IOException,
			com.qainfotech.tap.training.snl.api.PlayerExistsException,
			com.qainfotech.tap.training.snl.api.GameInProgressException,
			com.qainfotech.tap.training.snl.api.MaxPlayersReachedExeption,
			com.qainfotech.tap.training.snl.api.InvalidTurnException {
		int dice = 0;
	  Board	boad = new Board();
		JSONArray steps = boad.data.getJSONArray("steps");
		steps.getJSONObject(1).put("target", 97);
		steps.getJSONObject(2).put("target", 97);
		steps.getJSONObject(3).put("target", 97);
		steps.getJSONObject(4).put("target", 97);
		steps.getJSONObject(5).put("target", 97);
		steps.getJSONObject(6).put("target", 97);
		steps.getJSONObject(99).put("target", 99);
		BoardModel.save(boad.uuid, boad.data);
		boad.registerPlayer("siddharth");
		UUID uuid = UUID
				.fromString(((JSONObject) boad.getData().getJSONArray("players").get(0)).get("uuid").toString());
		JSONObject response = boad.rollDice(uuid);

		do {
			response = boad.rollDice(uuid);
			dice = response.getInt("dice");
			if (dice >= 4) {
				assertThat(response.getString("message")).isEqualTo("Incorrect roll of dice. Player did not move");
				break;
			}
		} while (true);
	}

	@Test
	public void gameProcessTest() throws InvalidTurnException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException {
		Board board1 = new Board();
		int position = 0;
		int type = 0;
		int turn = 0;
		board1.registerPlayer("sourav");
		board1.registerPlayer("gorav");
		int length = board1.data.getJSONArray("players").length();
		while (position < 100) {

			JSONObject student = ((JSONObject) board1.data.getJSONArray("players").get(turn));
			UUID uuid = UUID
					.fromString(((JSONObject) board1.data.getJSONArray("players").get(turn)).get("uuid").toString());
			position = ((JSONObject) board1.data.getJSONArray("players").get(turn)).getInt("position");
			JSONObject response = board1.rollDice(uuid);
			int dice = response.getInt("dice");
			position = position + dice;
			if (position <= 100)
				type = ((JSONObject) board1.data.getJSONArray("steps").get(position)).getInt("type");

			if (type == 0 && position <= 100) {
				assertThat(student.getInt("position")).isEqualTo(position);

			} else if (type == 1 && position <= 100) {

				position = ((JSONObject) board1.data.getJSONArray("steps").get(position)).getInt("target");
				assertThat(student.getInt("position")).isEqualTo(position);
				// assertThat(actual)
			} else if (type == 2 && position <= 100) {
				position = ((JSONObject) board1.data.getJSONArray("steps").get(position)).getInt("target");
				assertThat(student.getInt("position")).isEqualTo(position);
			}

			if (turn == length - 1)
				turn = 0;
			else
				turn = turn + 1;

		}

	}

	@Test
	public void delete_no_user_eith_such_UUID_exists() throws NoUserWithSuchUUIDException, IOException, PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption {
		Board board= new Board();
		board.registerPlayer("Piyush");
		board.registerPlayer("Piyush1");
		String name = ((JSONObject) board.data.getJSONArray("players").get(0)).get("name").toString();
		UUID uuid1 = UUID.fromString(((JSONObject) board.data.getJSONArray("players").get(0)).get("uuid").toString());
		board.deletePlayer(uuid1);
		assertThat(((JSONObject) board.data.getJSONArray("players").get(0)).getString("name")).isNotEqualTo(name);

	}
	
}
