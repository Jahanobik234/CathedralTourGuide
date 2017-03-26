package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

/**
 * Created by A on 3/26/2017.
 */

public class Question {

    private String question, choice1, choice2, choice3, answer;

    public Question(String question, String choice1, String choice2, String choice3, String answer)
    {
        this.question = question;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.answer = answer;
    }

    public String getQuestion()
    {
        return question;
    }

    public String getChoice1()
    {
        return choice1;
    }

    public String getChoice2()
    {
        return choice2;
    }

    public String getChoice3()
    {
        return choice3;
    }

    public String getAnswer()
    {
        return answer;
    }
}
