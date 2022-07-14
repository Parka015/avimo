package com.example.avimo;

public interface Regex {

    //Expresion regulares básicas
    public static String palabra = "([a-zA-ZÀ-ÿ\\u00f1\\u00d1])+";
    public static String numero = "\\d+";

    //Expresiones Regulares Fecha
    public static String adv_tiempo = "(hoy|mañana|pasado mañana)";
    public static String dia_semana = "(lunes|martes|miércoles|jueves|viernes|sábado|domingo)";
    public static String nombre_mes = "(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)";

    public static String numero_mes = "\\d{1,2}";
    public static String anio_4 = "\\d{4}";
    public static String anio_2 = "\\d{2}";
    public static String info_adicional1 = "de la (próxima|siguiente) semana";
    public static String info_adicional2 = "de la semana que viene";
    public static String dia_mes_anio = "\\d{1,2}( (del|de) ("+nombre_mes+"|"+numero_mes+")" + "( (del|de) "+anio_4+"| del "+anio_2+")?)?";


    //Expresiones Regulares Hora
    public static String info_adicional_minutos = "(y (\\d{1,2}|cuarto|media)|menos cuarto|menoscuarto)";
    public static String info_adicional_hora_dia = "de la (tarde|noche|mañana)";
    public static String info_adicional5 = ":\\d\\d";

    public static String hora = "\\d{1,2}" + "( "+info_adicional_minutos+"|"+info_adicional5+")?" + "( "+info_adicional_hora_dia+")?";
    public static String hora_ini = "a (las|la) "+hora;
    public static String hora_franja = "(desde|de)( las| la)? "+hora+" (hasta|a)( las| la)? "+hora;


    public static String fecha_unica = "("+adv_tiempo+"|("+dia_semana+"(( "+info_adicional1+"| "+info_adicional2+")| que viene)?)|"+dia_mes_anio+")"+"( "+hora_ini+"| "+hora_franja+")?";

    public static String fecha_franja_aux = "("+adv_tiempo+"|("+dia_semana+"(( "+info_adicional1+"| "+info_adicional2+")| que viene)?)|"+dia_mes_anio+")"+"( "+hora_ini+")?";

    public static String fecha_franja = "(del|desde|de|el)( el)? "+fecha_franja_aux+" (hasta|al|a|ha)( el)? "+fecha_franja_aux;


    // Expresiones regulares especificas de Listar Eventos
    public static String info_adicional3 = "esta semana";
    public static String info_adicional4 = "((próxima|siguiente) semana|semana que viene)";



    /****** Expresiones regulares completas *****/

    public static String regex_fecha = "("+fecha_unica+"|"+fecha_franja+")";
    public static String regex_titulo = "(de|con) (título|nombre) (("+palabra+"|"+numero+") )+(fin|film)";
    public static String regex_localizacion = "en (("+palabra+"|"+numero+") )+(fin|film)";
    public static String regex_tags = "(tags|tag) (("+palabra+" )+y "+palabra+"|("+palabra+")) (fin|film)";

    public static String regex_crear_evento = "(crea|créa)(\\w){0,5}( un)? evento";
    public static String regex_listar_evento = "(lista|lísta)\\w{0,5}( me)?( los)? evento";
    public static String regex_modificar_evento = "(modifica|modifíca)\\w{0,5}( me)?( el)? evento "+regex_titulo;
    public static String regex_buscar_evento = "(busca|búsca)\\w{0,5}( me)?( el| un)? evento "+regex_titulo;
    public static String regex_eliminar_evento = "(elimina|elimí)\\w{0,5}( me)?( el| un)? evento "+regex_titulo;


    /****** AYUDA *****/

    public static String regex_ayuda_listar_evento= "ayuda( a)? (listar|alistar)";
    public static String regex_ayuda_buscar_evento= "ayuda( a)? buscar";
    public static String regex_ayuda_modificar_evento= "ayuda( a)? modificar";
    public static String regex_ayuda_eliminar_evento= "ayuda( a)? eliminar";


    //Ayuda crear evento
    public static String regex_ayuda_crear_evento= "ayuda crear";
    public static String regex_mas_ayuda_crear_evento= "más ayuda crear";
    public static String regex_mas_ejemplos_crear_evento= "más ejemplos crear";


}
