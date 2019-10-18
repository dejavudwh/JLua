package compiler.ast;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// BNF
/*
* block ::= {stat} [retstat]
* retstat ::= return [explist] [';']
* explist ::= exp {',' exp}
*/
@Getter
@Setter
public class Block extends Node {

    private List<Stat> stats;
    private List<Exp> retExps;
}
