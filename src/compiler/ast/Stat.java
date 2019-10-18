package compiler.ast;

// BNF
/*
* stat ::= ';' |
*          varlist '=' explist |
*          functioncall |
*          label |
*          break |
*          goto Name |
*          do blocak end |
*          while exp do blocak end |
*          repeat block until exp |
*          if exp then block {elseif exp then block} [else bloack] end |
*          for Name ‘=’ exp ‘,’ exp [‘,’ exp] do block end |
*          for namelist in explist do block end |
*          function funcname funcbody |
*          local function Name funcbody |
*          local namelist [‘=’ explist]
 */

public abstract class Stat extends Node {
}
