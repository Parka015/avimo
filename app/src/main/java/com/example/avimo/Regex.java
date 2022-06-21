package com.example.avimo;

public interface Regex {

    //Expresion regulares básicas
    public static String palabra = "([a-zA-Z])+";
    public static String numero = "\\d+";

    //Expresiones Regulares Fecha
    public static String adv_tiempo = "(hoy|mañana|pasado mañana)";
    public static String dia_semana = "(lunes|martes|miércoles|jueves|viernes|sábado|domingo)";
    public static String mes = "(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)";

    public static String numero_mes = "[1-12]";
    public static String anio_4 = "\\d{4}";
    public static String anio_2 = "\\d{2}";
    public static String info_adicional1 = "de la (próxima|siguiente) semana";
    public static String info_adicional2 = "(de la semana que viene|que viene)";
    public static String dia_mes_anio = "\\d{1,2} (del|de) ("+mes+"|"+numero_mes+")( (del|de) "+anio_4+"| del "+anio_2+")?";


    //Expresiones Regulares Hora
    public static String info_adicional_minutos = "(y (\\d{1,2}|cuarto|media)|menos cuarto|menoscuarto)";
    public static String info_adicional_hora_dia = "de la (tarde|noche|mañana)";
    public static String info_adicional5 = ":\\d\\d";

    public static String hora = "\\d{1,2}" + "( "+info_adicional_minutos+"|"+info_adicional5+")?" + "( "+info_adicional_hora_dia+")?";
    public static String hora_ini = "a (las|la) "+hora;
    public static String hora_franja = "(desde|de)( las| la)? "+hora+" (hasta|a)( las| la)? "+hora;


    public static String fecha_unica = "("+adv_tiempo+"|("+dia_semana+"( "+info_adicional1+"| "+info_adicional2+")?)|"+dia_mes_anio+")"+"( "+hora_ini+"| "+hora_franja+")?";

    public static String dia_mes_anio_aux = "\\d{1,2}( (del|de) ("+mes+"|"+numero_mes+")" + "( (del|de) "+anio_4+"| del "+anio_2+")?)?";
    public static String fecha_franja_aux1 = "("+adv_tiempo+"|("+dia_semana+"( "+info_adicional1+"| "+info_adicional2+")?)|"+dia_mes_anio_aux+")"+"( "+hora_ini+")?";
    public static String fecha_franja_aux2 = "("+adv_tiempo+"|("+dia_semana+"( "+info_adicional1+"| "+info_adicional2+")?)|"+dia_mes_anio+")"+"( "+hora_ini+")?";

    public static String fecha_franja = "(del|desde|de)( el)? "+fecha_franja_aux1+" (hasta|al)( el)? "+fecha_franja_aux2;


    //Expresiones regulares completas
    public static String regex_fecha = "("+fecha_unica+"|"+fecha_franja+")";
    public static String regex_titulo = "(de|con) (título|nombre) (("+palabra+"|"+numero+") )+fin";
    public static String regex_localizacion = "localización en (("+palabra+"|"+numero+") )+fin";
    public static String regex_tags = "(tags|tag) (("+palabra+" )+y "+palabra+"|("+palabra+"))";

}
