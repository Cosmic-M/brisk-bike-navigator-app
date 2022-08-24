package brisk.bike.navigator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import com.google.android.gms.maps.model.LatLng;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import brisk.bike.navigator.db.MemoryPlaceBaseHelper;
import brisk.bike.navigator.db.MemoryPlaceCursorWrapper;
import brisk.bike.navigator.db.SchemaDB;
import brisk.bike.navigator.modul.MemoryPlace;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class PlaceLab {
    private static PlaceLab mPlaceLab;
    private final Context mContext;
    private final SQLiteDatabase mBase;

    public static PlaceLab get(Context context){
        if (mPlaceLab == null){
            mPlaceLab = new PlaceLab(context);
        }
        return mPlaceLab;
    }

    private PlaceLab(Context context){
        mContext = context;
        mBase = new MemoryPlaceBaseHelper(context).getWritableDatabase();
        boolean flag = SharedPreferences.getSharedPreferenceFlag(context);
        if (!flag) {
            MemoryPlace mp;
            mp = new MemoryPlace(new LatLng(49.784349,36.579804));
            mp.setTextDescription("Одне з найкрасивіших місць Харківщини – гребля на річці "
                    + "Сіверський Дінець, розташована біля смт Есхар за течією. На лівому "
                    + "березі річки знаходиться заплавний ліс. Прогулюючись лісом, можна "
                    + "побачити вікові дуби та боброві нори.");
            saveImageToFileSystem(context, mp, R.raw.plotina_eshar);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.760011,36.670967));
            mp.setTextDescription("Дорога до смт Малинівка. Цікаві факти з Вікіпедії: "
                    + "за версією В.М. Миславського, викладеної у довіднику «Харків та кіно»,"
                    + " виданому 2004 року, деякі натуральні зйомки фільму \"Весілля у Малинівці\""
                    + " проводились у Малинівці у Чугуївському районі. Але на більш поширену "
                    + "думку, це було у Малинівці, що у Глобинському районі Полтавської області,"
                    + " а сам фільм знятий у кількох селах Лубенського району Полтавської області."
                    + " У Малинівці історично всі поперечні вулиці називаються «сотнями» за "
                    + "номерами козацьких підрозділів військового поселення: 1 сотня, 6 сотня."
                    + " Усього 13 сотень + 14 – цвинтар. А у танковому симуляторі World Of Tanks"
                    + " є карта Малинівки.");
            saveImageToFileSystem(context, mp, R.raw.malinovka_road);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.614979,36.321349));
            mp.setTextDescription("Дитячий табір «Романтик» було збудовано на початку 70-х і є"
                    + " базою відпочинку Харківського приладобудівного заводу ім. Шевченка – "
                    + "одного з найбільших заводів у СРСР із виробництва військової електроніки"
                    + " та побутової техніки. Останню зміну відпочиваючих табір приймав у далекому"
                    + " 2004 році. З того часу база дрейфує за часом: територія заросла бур'яном,"
                    + " споруди занепали і почали обсипатися, а пам'ятники на території частково"
                    + " зруйновані. Все це робить непогану заявочку на декорації до зйомок "
                    + " продовження Божевільного Макса. А загалом відмінне атмосферне місце, "
                    + "яке варто відвідати, якщо ще не встигли.");
            saveImageToFileSystem(context, mp, R.raw.lager_romantic);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.851276,36.822684));
            mp.setTextDescription("Кицевська пустеля – дуже незвичайне та мальовниче місце. "
                    + "Колись у цих краях проводилися танкові навчання, про що свідчать нерідкі "
                    + "знахідки танкових снарядів. Ну а зараз тут можна насолоджуватися тишею "
                    + "та відсутністю міської суєти.");
            saveImageToFileSystem(context, mp, R.raw.dessert_in_kicevka);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.675567,36.289629));
            mp.setTextDescription("Станція вивчення іоносфери, м. Зміїв. Практично перед розвалом"
                    + " Радянського Союзу під Харковом була побудована станція іоносферних "
                    + "досліджень, яка була прямим аналогом американського проекту HAARP на "
                    + "Алясці, який успішно функціонує і сьогодні. Комплекс станції складався "
                    + "з кількох антенних полів та гігантської параболічної антени діаметром 25"
                    + " метрів, здатної випромінювати потужність близько 25 МВт.");
            saveImageToFileSystem(context, mp, R.raw.zmiyov_stancia_ionosferi);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.555767,36.311048));
            mp.setTextDescription("Заїзд у ліс стартує з околиці с. Велика Гомольша. Далі "
                    + "покажчики запропонують кілька варіантів розвитку подій. Ми ж обираємо "
                    + "недитяче випробування – висунутись після водозбору у напрямку Гайдар. "
                    + "По ходу руху не раз виникатиме незручне почуття, що дорога ось-ось упреться"
                    + " в чагарники, не розраховані під наш транспорт. Місцями зустрічаються "
                    + "повалені впоперек дороги дерева, підкріплюючи те незручне почуття, але "
                    + "повертати назад вже пізно - ми проїхали занадто далеко. Відразу обмовлюся,"
                    + " що при таких розкладах пересуватися лісом ви будете не одні: кілька "
                    + "восьмилапих, яких ви підберете з кущів та дерев, стануть вірними "
                    + "супутниками на час поїздки через ліс.");
            saveImageToFileSystem(context, mp, R.raw.gomilshansk);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.750397, 36.537137));
            mp.setTextDescription("Суворівське джерело, або, як його ще називають \"Руда криниця\""
                    + " (просто у воді підвищений вміст заліза, і це видно неозброєним оком), "
                    + "знаходиться у Зміївському районі Харківської області, що на березі "
                    + "річки Сіверський Донець.");
            saveImageToFileSystem(context, mp, R.raw.suvorov_origin);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.658841,36.350675));
            mp.setTextDescription("Зміївські Кручі знаходяться поблизу південної околиці "
                    + "м. Зміїва. Підніжжя \"Зміївських гір\" розташоване безпосередньо "
                    + "біля річки Сіверський Донець");
            saveImageToFileSystem(context, mp, R.raw.zmiiv);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.5391071,36.4051505));
            mp.setTextDescription("Пам'ятник 18 бійцям та командирам 1153-го стрілецького полку"
                    + " 343-ї стрілецької дивізії біля дороги у с. Нижній Бішкін.");
            saveImageToFileSystem(context, mp, R.raw.lower_bishkin);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.439806,36.683824));
            mp.setTextDescription("Шебелінський крейдяний кар'єр, немов великий каньйон, "
                    + "приваблює туристів для огляду величного техногенного масиву. Для "
                    + "мешканців «Першої» столиці шлях неблизький, проте відвідування цього "
                    + "чудового та незвичайного місця принесе велике задоволення всім любителям "
                    + "екскурсійних вело-або мототурів.");
            saveImageToFileSystem(context, mp, R.raw.career_of_chalk);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.787693, 36.528683));
            mp.setTextDescription("Колишня ракетна база ППО (п. Есхар) на сьогоднішній день є"
                    + " абсолютно занедбаною");
            saveImageToFileSystem(context, mp, R.raw.rocket_base);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.178752,37.277947));
            mp.setTextDescription("Геологічна пам'ятка природи та історичне ядро міста. Місце,"
                    + "де річка Сіверський Донець огинає величезний виступ верхнемелових та "
                    + "юрських порід, а також найвища точка Харківської області, а саме 218 м "
                    + "над рівнем моря... Здається, всі й так уже здогадалися, що йдеться про "
                    + "гору Кременець. На схилі гори встановлено зібрані на околицях кам'яні "
                    + "боввани статуї. Згідно з давньою легендою, племена, що жили в степу, "
                    + "поклонялися богу-сонцю, але прогнівали його і були перетворені на камінь."
                    + "Хто не вірить – може приїхати і подивитися: датовані серединою XII ст.,"
                    + " вони дають уявлення, хоч і невиразне (час все ж таки наклав свій відбиток)");
            saveImageToFileSystem(context, mp, R.raw.the_kremyanec_mountain);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(50.258176, 36.830691));
            mp.setTextDescription("Одне з найцікавіших і наймальовничиших місць Харківщини - "
                    + "довга гряда крейдяних скель, що простягаються на кілька десятків кілометрів"
                    + " уздовж річки Оскіл. Це залишки древнього моря, яке існувало тут 70 млн"
                    + " років тому");
            saveImageToFileSystem(context, mp, R.raw.chalk_mountain);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(48.660062, 37.685379));
            mp.setTextDescription("Білокузьминівські крейдяні скелі – дивовижний витвір природи. "
                    + "Вони знаходяться за 500 м від центру села Білокузьминівка Костянтинівського"
                    + " району Донецької області, за що і однойменно названі. У 1972 році скелі "
                    + "отримали статус геологічної пам’ятки природи місцевого значення та з того "
                    + "ж часу охороняються. Неймовірним видом Білокузьминівських крейдяних скель "
                    + "та їх околицями милуються місцеві жителі та приїжджі туристи.");
            saveImageToFileSystem(context, mp, R.raw.belokuzminovka);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.585458, 36.349820));
            mp.setTextDescription("Видатною природною пам’яткою Національного природного парку "
                    + "«Гомільшанські ліси» є «Козача гора», яка нависає над повноводним Донцем. "
                    + "Легенди «Козачої гори», передані з вуст в уста, оповідають про битви "
                    + "козацької вольниці за свою свободу і незалежність.");
            saveImageToFileSystem(context, mp, R.raw.kossak_hillside);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(49.981158, 35.758054));
            mp.setTextDescription("Коли в другій половині XVII століття (перша документальна "
                    + "згадка від 1665 року) на сторінках українських літописів з’являється "
                    + "велике село Всіхсвятське мало кому могло спасти на думку, що не мине й "
                    + "трьох десятиліть, коли його землі будуть поділені на дві самостійні, "
                    + "підпорядковані різним господарям територіальні одиниці: Добропілля та "
                    + "Мерчик (Старий), що став справжньою легендою завдяки своєму унікальному"
                    + " садибному комплексу.");
            saveImageToFileSystem(context, mp, R.raw.old_merchik);
            insertPlaceIntoDB(mp);

            mp = new MemoryPlace(new LatLng(50.044141, 35.430567));
            mp.setTextDescription("Композицію парку Кеніга створив ландшафтний архітектор "
                    + "Георг Куфальдт. Під його керівництвом було висаджено близько 150 видів "
                    + "екзотичних для цієї місцевості рослин. Також було побудовано безліч алей,"
                    + "фонтанів, сходів і інших декоративних елементів. Площа парку становить "
                    + "близько 39,3 га. Родзинкою і сьогодні залишається липова алея, гілки дерев"
                    + " якій ростуть вертикально вгору. ");
            saveImageToFileSystem(context, mp, R.raw.sharovka);
            insertPlaceIntoDB(mp);

            SharedPreferences.setSharedPreferenceFlag(context, true);
        }
    }

    private MemoryPlaceCursorWrapper getCursorWrapper(String name, String clause, String[] args){
        Cursor cursor = mBase.query(
                name,
                null,
                clause,
                args,
                null,
                null,
                null
        );
        return new MemoryPlaceCursorWrapper(cursor);
    }

    private ContentValues getContentValues(MemoryPlace mp){
        ContentValues cv = new ContentValues();
        cv.put(SchemaDB.Cols.LATITUDE, mp.getLatLng().latitude);
        cv.put(SchemaDB.Cols.LONGITUDE, mp.getLatLng().longitude);
        cv.put(SchemaDB.Cols.FILE_IMAGE_NAME, mp.getPhotoFileName());
        cv.put(SchemaDB.Cols.DESCRIPTION, mp.getTextDescription());
        return cv;
    }

    public void insertPlaceIntoDB(MemoryPlace mp){
        ContentValues values = getContentValues(mp);
        mBase.insert(SchemaDB.TABLE_NAME, null, values);
    }

    public List<MemoryPlace> getMemoryPlace(){
        MemoryPlace memoryPlace;
        List<MemoryPlace> listPlaces = new ArrayList<>();
        MemoryPlaceCursorWrapper cursor = getCursorWrapper(SchemaDB.TABLE_NAME, null, null);
        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                memoryPlace = cursor.getPlace();
                listPlaces.add(memoryPlace);
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return listPlaces;
    }

    public int removeRowDbById(int id){
        return mBase.delete(SchemaDB.TABLE_NAME,
                SchemaDB.Cols.ID + " =? ", new String[]{String.valueOf(id)});
    }

    private void saveImageToFileSystem(Context context, MemoryPlace memoryPlace, int resource){
        InputStream in = context.getResources().openRawResource(resource);
        try {
            byte buffer[] = new byte[in.available()];
            in.read(buffer);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(buffer);

            File file = getPhotoFile(memoryPlace);
            FileOutputStream fos = new FileOutputStream(file);

            baos.writeTo(fos);
        }
        catch (FileNotFoundException exc){
            exc.getMessage();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getPhotoFile(MemoryPlace place){
        File fileExternalDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (fileExternalDir == null){
            return null;
        }
        return new File(fileExternalDir, place.getPhotoName());
    }
}
