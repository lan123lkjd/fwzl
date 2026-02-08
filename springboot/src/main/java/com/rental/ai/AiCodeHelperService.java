package com.rental.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

import java.util.List;

//@AiService//springboot会自动生成对应的Bean，要注释掉原来配置注解
public interface AiCodeHelperService {
    @SystemMessage(fromResource = "system_prompt.txt")
    String chat(String userMassage);

    Report chatForReport(String userMassage);

    record Report(String name, List<String> suggestionlist) {
    }

    // 返回封装之后的结果
    @SystemMessage(fromResource = "system_prompt.txt")
    Result<String> chatWithRag(String userMassage);

    @SystemMessage(fromResource = "system_prompt.txt")
    Flux<String> chatStream(@MemoryId long memoryId, @UserMessage String message);

}
