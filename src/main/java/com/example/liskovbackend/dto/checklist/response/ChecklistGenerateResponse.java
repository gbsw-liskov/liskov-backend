package com.example.liskovbackend.dto.checklist.response;

import com.example.liskovbackend.dto.model.response.Choice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ChecklistGenerateResponse {
//   "{
//  ""choices"": [
//    {
//      ""index"": 0,
//      ""message"": {
//        ""content"": ""경량성; 직관적 구문; 동적 타입; 풍부한 표준 라이브러리; 가독성 높은 코드; 객체지향; 인터프리터 언어; 풍부한 타이핑 옵션"",
//      },
//    }
//  ],
//} "
    private String content;
}
