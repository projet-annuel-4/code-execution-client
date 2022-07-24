package com.projet_annuel.rec.controller;

import com.google.gson.Gson;
import com.projet_annuel.rec.dto.CodeRequest;
import com.projet_annuel.rec.dto.CodeResponse;
import com.projet_annuel.rec.dto.CodeResponse2;
import com.projet_annuel.rec.repository.CodeResponseDao;
import com.projet_annuel.rec.service.RedisService;
import com.projet_annuel.rec.configuration.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/executor")
public class CodeExecutionController {

    private final RedisService redisService;
    private final RpcClient rpcClient;
    private final CodeResponseDao codeResponseDao;
    public static final String HASH_KEY = "CodeResponse";
    private final Gson gson = new Gson();

    @Autowired
    public CodeExecutionController(final RedisService redisService, final RpcClient rpcClient, final CodeResponseDao codeResponseDao) {
        this.redisService = redisService;
        this.rpcClient = rpcClient;
        this.codeResponseDao = codeResponseDao;
    }

    @PostMapping("/submit")
    public ResponseEntity<CodeResponse> saveCode(@RequestBody final CodeRequest code){
        System.out.println(code.toString());
        if( code.isTest()){
            code.setId(UUID.randomUUID().getMostSignificantBits());
        }
        CodeResponse codeAlreadyExecuted = gson.fromJson( (String) redisService.getById(HASH_KEY, code.getId().toString() ), CodeResponse.class);
        if( codeAlreadyExecuted != null){
            System.out.println("je suis rentré !!!!");
            return ResponseEntity.ok(codeAlreadyExecuted);
        }

        String response = rpcClient.send( code );
        System.out.println( "ma reponse /submit : " + response);
        return getResults( response, code );
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<CodeResponse> getResults(@PathVariable final String id, CodeRequest codeRequest){
        System.out.println("id du get " + id);
        CodeResponse response = gson.fromJson( (String) redisService.getById(HASH_KEY, id ), CodeResponse.class);
        System.out.println("ma response = " + response);
        if( codeRequest.isTest()){
            redisService.deleteById(HASH_KEY, id);
            System.out.println("c'est un test");
        }
        CodeResponse finalResponse = new CodeResponse(response.getId(), response.getOutput(), response.getError(), response.getStatus());
        System.out.println(response);
        return ResponseEntity.ok(finalResponse);
    }
}
