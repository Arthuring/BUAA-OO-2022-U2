import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;

public class RequestList {
    private final ArrayList<PersonRequest> requests = new ArrayList<>();
    private PersonRequest nowRequest;
    private int nowRequestNum;

    public RequestList(ArrayList<PersonRequest> requestList) {
        this.requests.addAll(requestList);
        this.nowRequestNum = -1;
        this.nowRequest = null;
    }

    public RequestList(int fromFloor, int toFloor, char fromBuilding, char toBuilding, int id) {
        PersonRequest r = new PersonRequest(fromFloor, toFloor, fromBuilding, toBuilding, id);
        requests.add(r);
        nowRequest = r;
        nowRequestNum = 0;
    }

    public int getNowRequestNum() {
        return nowRequestNum;
    }

    public PersonRequest nowRequest() {
        return nowRequest;
    }

    public void goToNext() {
        nowRequestNum += 1;
        this.nowRequest = requests.get(nowRequestNum);
    }

    public boolean hasNext() {
        return nowRequestNum < requests.size() - 1;
    }

    public String toString() {
        StringJoiner sj = new StringJoiner(";");
        for (PersonRequest request : requests) {
            sj.add(request.toString());
        }
        return sj.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestList that = (RequestList) o;
        return Objects.equals(this.toString(), that.toString());
    }

}
